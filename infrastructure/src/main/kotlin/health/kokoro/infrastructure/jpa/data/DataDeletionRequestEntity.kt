package health.kokoro.infrastructure.jpa.data

import health.kokoro.infrastructure.jpa.BaseEntity
import health.kokoro.infrastructure.jpa.user.UserEntity
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "data_deletion_requests")
class DataDeletionRequestEntity(
    @JoinColumn("user_id") @OneToOne var user: UserEntity,
    @Column("confirmed_at") var confirmedAt: Instant?,
    @Column("confirmation_code") var confirmationCode: String?,
    @Column("code_requested_at") var codeRequestedAt: Instant
) : BaseEntity()