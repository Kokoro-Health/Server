package health.kokoro.domain.model.journal

import java.time.Instant
import java.util.*

data class JournalEntry(
    val id: UUID?,
    val content: String,
    val availableUntil: Instant?,
    val createdAt: Instant,
    val userId: UUID
) {
    companion object {
        val EMPTY = JournalEntry(
            id = null,
            content = "",
            availableUntil = null,
            createdAt = Instant.now(),
            userId = UUID.randomUUID()
        )
    }
}
