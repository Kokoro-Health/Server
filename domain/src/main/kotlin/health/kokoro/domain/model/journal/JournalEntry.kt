package health.kokoro.domain.model.journal

import java.time.Instant
import java.util.*

data class JournalEntry(
    val id: UUID?,
    val content: String,
    val lockedAt: Instant,
    val createdAt: Instant,
    val updatedAt: Instant,
    val userId: UUID
) {
    companion object {
        val EMPTY = JournalEntry(
            id = null,
            content = "",
            lockedAt = Instant.now(),
            createdAt = Instant.now(),
            userId = UUID.randomUUID(),
            updatedAt = Instant.now()
        )
    }
}
