package health.kokoro.infrastructure.jpa.audit

import health.kokoro.domain.model.audit.AuditEvent
import health.kokoro.infrastructure.jpa.user.UserEntity
import health.kokoro.infrastructure.jpa.user.UserJpaRepository
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class AuditEventMapper(
    private val userJpa: UserJpaRepository
) {
    fun toEntity(domain: AuditEvent): AuditEventEntity {
        val user: UserEntity? = domain.userId?.let { userJpa.findById(it).getOrNull() }
        return AuditEventEntity(
            user = user,
            action = domain.action,
            ipAddress = domain.ipAddress,
            userAgent = domain.userAgent,
            metadata = domain.metaData
        )
    }

    fun toDomain(entity: AuditEventEntity): AuditEvent {
        return AuditEvent(
            id = entity.id!!,
            userId = entity.user?.id!!,
            action = entity.action,
            ipAddress = entity.ipAddress,
            userAgent = entity.userAgent,
            timeStamp = entity.createdAt,
            metaData = entity.metadata
        )
    }
}