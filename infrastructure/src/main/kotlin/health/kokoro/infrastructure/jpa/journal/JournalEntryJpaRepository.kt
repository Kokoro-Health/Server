package health.kokoro.infrastructure.jpa.journal

import health.kokoro.infrastructure.jpa.user.UserEntity
import java.time.Instant
import java.util.*

interface JournalEntryJpaRepository : org.springframework.data.jpa.repository.JpaRepository<JournalEntryEntity, UUID> {
    fun findFirstByUserIdAndUpdatedAtAfterOrderByCreatedAtDesc(
        userId: UUID,
        updatedAtAfter: Instant
    ): JournalEntryEntity?

    fun findAllByUserId(uuid: UUID): List<JournalEntryEntity>
    fun user(user: UserEntity): MutableList<JournalEntryEntity>
}