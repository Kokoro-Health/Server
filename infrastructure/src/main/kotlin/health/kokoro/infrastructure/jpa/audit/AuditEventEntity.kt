package health.kokoro.infrastructure.jpa.audit

import health.kokoro.domain.model.audit.AuditAction
import health.kokoro.infrastructure.converter.MapConverter
import health.kokoro.infrastructure.jpa.BaseEntity
import health.kokoro.infrastructure.jpa.user.UserEntity
import jakarta.persistence.*

@Entity
@Table(name = "audit_events")
data class AuditEventEntity(
    @JoinColumn("user_id") @ManyToOne val user: UserEntity?,
    @Enumerated(EnumType.STRING) @Column("action") val action: AuditAction,
    @Column("ip_address") val ipAddress: String,
    @Column("user_agent") val userAgent: String,
    @Column("metadata", length = 1028) @Convert(converter = MapConverter::class) val metadata: Map<String, String>?
) : BaseEntity()