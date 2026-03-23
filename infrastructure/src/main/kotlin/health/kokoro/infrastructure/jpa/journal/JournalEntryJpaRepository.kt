package health.kokoro.infrastructure.jpa.journal

import java.time.Instant
import java.util.*

interface JournalEntryJpaRepository : org.springframework.data.jpa.repository.JpaRepository<JournalEntryEntity, UUID> {
    fun findFirstByUserIdAndUpdatedAtAfterOrderByCreatedAtDesc(
        userId: UUID,
        updatedAtAfter: Instant
    ): JournalEntryEntity?
}