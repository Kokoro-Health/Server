package health.kokoro.infrastructure.jpa.journal

import health.kokoro.domain.model.journal.JournalEntry
import health.kokoro.domain.port.security.EncryptionPort
import health.kokoro.infrastructure.jpa.user.UserJpaRepository
import org.springframework.stereotype.Component

@Component
class JournalEntryMapper(
    private val encryptionPort: EncryptionPort,
    private val userJpaRepository: UserJpaRepository
) {
    fun toDomain(entity: JournalEntryEntity): JournalEntry {
        val availableUntil = entity.updatedAt.plusSeconds(30 * 60)
        return JournalEntry(
            id = entity.id!!,
            content = encryptionPort.decrypt(entity.content),
            createdAt = entity.createdAt,
            userId = entity.user.id!!,
            availableUntil = availableUntil
        )
    }

    fun toEntity(domain: JournalEntry): JournalEntryEntity {
        val user = userJpaRepository.findById(domain.userId).orElseThrow()
        return JournalEntryEntity(
            content = domain.content.let { encryptionPort.encrypt(it) },
            user = user
        )
    }
}