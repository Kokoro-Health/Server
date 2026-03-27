package health.kokoro.infrastructure.jpa.user.security.passkey

import health.kokoro.domain.model.security.EncryptedData
import health.kokoro.domain.model.user.security.passkey.ChallengeType
import health.kokoro.infrastructure.converter.EncryptedDataConverter
import health.kokoro.infrastructure.jpa.BaseEntity
import health.kokoro.infrastructure.jpa.user.UserEntity
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "passkey_challenges")
class PasskeyChallengeEntity(
    @JoinColumn(name = "user_id") @ManyToOne var user: UserEntity?,
    @Column("email") var email: String?,
    @Column("type") @Enumerated(EnumType.STRING) var type: ChallengeType,
    @Column(
        "data",
        columnDefinition = "TEXT"
    ) @Convert(converter = EncryptedDataConverter::class) var data: EncryptedData,
    @Column("expires_at") var expiresAt: Instant
) : BaseEntity()