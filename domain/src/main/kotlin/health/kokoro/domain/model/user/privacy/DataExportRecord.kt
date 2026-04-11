package health.kokoro.domain.model.user.privacy

import java.time.Instant
import java.util.*

data class DataExportRecord(
    val id: UUID?,
    val userId: UUID,
    val requestedAt: Instant,
    val completedAt: Instant?,
    val status: DataExportStatus,
    val ipAddress: String,
    val userAgent: String
)

enum class DataExportStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED
}
