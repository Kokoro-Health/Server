package health.kokoro.infrastructure.jpa.data

import health.kokoro.infrastructure.jpa.BaseEntity
import health.kokoro.infrastructure.jpa.user.UserEntity
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "data_deletion_requests")
data class DataDeletionRequestEntity(
    @JoinColumn("user_id") @OneToOne val user: UserEntity,
    @Column("confirmed_at") val confirmedAt: Instant?,
    @Column("confirmation_code") val confirmationCode: String?,
    @Column("code_requested_at") val codeRequestedAt: Instant
) : BaseEntity()