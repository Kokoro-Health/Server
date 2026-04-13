package health.kokoro.domain.port.audit

import health.kokoro.domain.model.audit.AuditEvent
import java.util.*

interface AuditEventRepository {
    fun add(event: AuditEvent): AuditEvent
    fun getByUserId(id: UUID): List<AuditEvent>
    fun deleteAllByUserId(userId: UUID)
}