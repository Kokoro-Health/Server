package health.kokoro.domain.model.data

import java.time.Instant
import java.util.*

data class DataDeletionRequest(
    val id: UUID,
    val userId: UUID,
    val createdAt: Instant,
    val confirmed: Boolean,
    val codeRequestedAt: Instant,
    val confirmedAt: Instant?,
    val confirmationCode: String?
)