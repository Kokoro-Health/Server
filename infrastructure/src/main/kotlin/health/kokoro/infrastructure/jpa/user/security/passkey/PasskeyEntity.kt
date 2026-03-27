package health.kokoro.infrastructure.jpa.user.security.passkey

import health.kokoro.domain.model.security.EncryptedData
import health.kokoro.infrastructure.converter.EncryptedDataConverter
import health.kokoro.infrastructure.jpa.BaseEntity
import health.kokoro.infrastructure.jpa.user.UserEntity
import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "passkeys")
class PasskeyEntity(
    @JoinColumn(name = "user_id") @ManyToOne var user: UserEntity,
    @Column(name = "credential_id", length = 512) var credentialId: String,
    @Column(name = "public_key") var publicKey: ByteArray,
    @Column(name = "sign_count") var signCount: Long,
    @Column(name = "device_name") @Convert(converter = EncryptedDataConverter::class) var deviceName: EncryptedData,
    @Column(name = "last_used_at") var lastUsedAt: Instant?
) : BaseEntity()