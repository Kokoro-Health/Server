package health.kokoro.infrastructure.jpa.user.security

import health.kokoro.domain.model.security.EncryptedData
import health.kokoro.infrastructure.converter.EncryptedDataConverter
import health.kokoro.infrastructure.jpa.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "user_security")
class UserSecurityEntity(
    @Column("password_hash") @Convert(converter = EncryptedDataConverter::class) var passwordHash: EncryptedData,
    @Column("mfa_enabled") var mfaEnabled: Boolean,
    @Column("mfa_secret") @Convert(converter = EncryptedDataConverter::class) var mfaSecret: EncryptedData?,
    @Column("verified") var verified: Boolean,
    @Column("verification_code") @Convert(converter = EncryptedDataConverter::class) var verificationCode: EncryptedData?,
    @Column("verification_code_requested_at") var verificationCodeRequestedAt: Instant?,
    @Column("password_reset_code") var passwordResetCode: String?,
    @Column("password_reset_code_requested_at") var passwordResetCodeRequestedAt: Instant?
) : BaseEntity()