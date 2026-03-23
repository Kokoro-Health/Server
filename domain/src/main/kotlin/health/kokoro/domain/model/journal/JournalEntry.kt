package health.kokoro.domain.model.journal

import java.time.Instant
import java.util.*

data class JournalEntry(
    val id: UUID,
    val content: String,
    val createdAt: Instant,
    val userId: UUID
)