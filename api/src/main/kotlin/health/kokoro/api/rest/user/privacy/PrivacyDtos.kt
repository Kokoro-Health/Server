package health.kokoro.api.rest.user.privacy

import java.time.Instant
import java.util.*

data class DataExportStatusResponseDto(
    val exportId: UUID,
    val status: String,
    val requestedAt: Instant,
    val completedAt: Instant?
)
