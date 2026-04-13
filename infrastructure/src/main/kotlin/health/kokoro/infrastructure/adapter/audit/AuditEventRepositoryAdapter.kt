package health.kokoro.infrastructure.adapter.audit

import health.kokoro.domain.model.audit.AuditEvent
import health.kokoro.domain.port.audit.AuditEventRepository
import health.kokoro.infrastructure.jpa.audit.AuditEventJpaRepository
import health.kokoro.infrastructure.jpa.audit.AuditEventMapper
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class AuditEventRepositoryAdapter(
    private val jpa: AuditEventJpaRepository,
    private val mapper: AuditEventMapper
) : AuditEventRepository {
    override fun add(event: AuditEvent): AuditEvent {
        return mapper.toEntity(event).let { mapper.toDomain(jpa.save(it)) }
    }

    override fun getByUserId(id: UUID): List<AuditEvent> {
        return jpa.getAllByUserId(id).map { mapper.toDomain(it) }
    }

    override fun deleteAllByUserId(userId: UUID) {
        jpa.deleteAllByUserId(userId)
    }
}