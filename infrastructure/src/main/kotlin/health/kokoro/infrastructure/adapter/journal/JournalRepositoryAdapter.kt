package health.kokoro.infrastructure.adapter.journal

import health.kokoro.domain.model.journal.JournalEntry
import health.kokoro.domain.port.journal.JournalRepository
import health.kokoro.domain.port.security.EncryptionPort
import health.kokoro.infrastructure.jpa.journal.JournalEntryJpaRepository
import health.kokoro.infrastructure.jpa.journal.JournalEntryMapper
import health.kokoro.infrastructure.jpa.user.UserJpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Repository
@Transactional
class JournalRepositoryAdapter(
    private val jpa: JournalEntryJpaRepository,
    private val encryptionPort: EncryptionPort,
    private val mapper: JournalEntryMapper,
    private val userJpaRepository: UserJpaRepository
) : JournalRepository {
    private val availabilityWindowMinutes = 30L

    override fun getCurrentJournal(userId: UUID): JournalEntry {
        val threshold = Instant.now().minusSeconds(availabilityWindowMinutes * 60)
        val journal = jpa.findFirstByUserIdAndUpdatedAtAfterOrderByCreatedAtDesc(userId, threshold)
            ?.let(mapper::toDomain)

        return journal ?: JournalEntry.EMPTY
    }

    override fun save(userId: UUID, id: UUID?, content: String): JournalEntry {
        val now = Instant.now()
        val threshold = now.minusSeconds(availabilityWindowMinutes * 60)
        val existing = jpa.findFirstByUserIdAndUpdatedAtAfterOrderByCreatedAtDesc(userId, threshold)

        if (existing != null) {
            val availableUntil = existing.updatedAt.plusSeconds(availabilityWindowMinutes * 60)
            if (availableUntil.isBefore(now)) {
                return mapper.toDomain(existing)
            }
            if (id != existing.id && id != null) {
                return JournalEntry.EMPTY
            }
            if (content.isBlank()) {
                jpa.delete(existing)
                return JournalEntry.EMPTY
            }
            existing.content = encryptionPort.encrypt(content)
            return jpa.save(existing).let { mapper.toDomain(it) }
        }

        if (content.isBlank()) {
            return JournalEntry.EMPTY
        }

        if (id != null) {
            throw IllegalArgumentException("This journal entry has been locked.")
        }

        val user = userJpaRepository.findById(userId).orElseThrow()
        val entity = JournalEntry(
            id = null,
            content = content,
            userId = user.id!!,
            lockedAt = Instant.now(),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
        return jpa.save(mapper.toEntity(entity)).let { mapper.toDomain(it) }
    }

    override fun getById(uuid: UUID): JournalEntry? {
        return jpa.findById(uuid).getOrNull()?.let { mapper.toDomain(it) }
    }

    override fun getAllByUserId(userId: UUID): List<JournalEntry> {
        return jpa.findAllByUserId(userId).map { mapper.toDomain(it) }
    }
}