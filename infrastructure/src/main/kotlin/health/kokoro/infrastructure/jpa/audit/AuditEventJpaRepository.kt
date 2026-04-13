package health.kokoro.infrastructure.jpa.audit

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface AuditEventJpaRepository : JpaRepository<AuditEventEntity, UUID> {
    fun getAllByUserId(id: UUID): List<AuditEventEntity>

    fun deleteAllByUserId(userId: UUID)
}