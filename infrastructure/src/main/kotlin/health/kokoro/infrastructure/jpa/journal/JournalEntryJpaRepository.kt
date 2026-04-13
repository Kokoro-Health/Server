package health.kokoro.infrastructure.jpa.journal

import health.kokoro.infrastructure.jpa.user.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.Instant
import java.util.*

interface JournalEntryJpaRepository : JpaRepository<JournalEntryEntity, UUID> {
    fun findFirstByUserIdAndUpdatedAtAfterOrderByCreatedAtDesc(
        userId: UUID,
        updatedAtAfter: Instant
    ): JournalEntryEntity?

    fun findAllByUserId(uuid: UUID): List<JournalEntryEntity>
    fun user(user: UserEntity): MutableList<JournalEntryEntity>

    fun deleteAllByUserId(userId: UUID)
}