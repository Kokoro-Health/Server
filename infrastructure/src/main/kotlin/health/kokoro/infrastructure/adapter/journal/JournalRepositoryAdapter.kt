package health.kokoro.infrastructure.adapter.journal

import health.kokoro.domain.model.journal.JournalEntry
import health.kokoro.domain.port.journal.JournalRepository
import health.kokoro.domain.port.security.EncryptionPort
import health.kokoro.infrastructure.jpa.journal.JournalEntryEntity
import health.kokoro.infrastructure.jpa.journal.JournalEntryJpaRepository
import health.kokoro.infrastructure.jpa.journal.JournalEntryMapper
import health.kokoro.infrastructure.jpa.user.UserJpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Repository
@Transactional
class JournalRepositoryAdapter(
    private val jpa: JournalEntryJpaRepository,
    private val encryptionPort: EncryptionPort,
    private val mapper: JournalEntryMapper,
    private val userJpaRepository: UserJpaRepository
) : JournalRepository {

    private val availabilityWindowMinutes = 30L

    override fun getCurrentJournal(userId: UUID): JournalEntry? {
        val threshold = Instant.now().minusSeconds(availabilityWindowMinutes * 60)
        val journal = jpa.findFirstByUserIdAndUpdatedAtAfterOrderByCreatedAtDesc(userId, threshold)
            ?.let(mapper::toDomain)
        return journal
    }

    override fun save(userId: UUID, content: String): JournalEntry {
        if (content.isBlank()) {
            getCurrentJournal(userId)?.let(mapper::toEntity)?.let(jpa::delete)
            return JournalEntry(content = "", id = UUID.randomUUID(), userId = userId, createdAt = Instant.now())
        }

        val user = userJpaRepository.findById(userId).orElseThrow()
        val threshold = Instant.now().minusSeconds(availabilityWindowMinutes * 60)
        val existing = jpa.findFirstByUserIdAndUpdatedAtAfterOrderByCreatedAtDesc(userId, threshold)

        val entity = if (existing != null) {
            existing.content = encryptionPort.encrypt(content)
            existing
        } else {
            JournalEntryEntity(
                user = user,
                content = encryptionPort.encrypt(content),
            )
        }

        return jpa.save(entity).let { mapper.toDomain(it) }
    }
}