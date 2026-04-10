package health.kokoro.domain.model.audit

import java.time.Instant
import java.util.*

data class AuditEvent(
    val id: UUID,
    val userId: UUID?,
    val action: AuditAction,
    val ipAddress: String,
    val userAgent: String,
    val timeStamp: Instant,
    val metaData: Map<String, String>?
)