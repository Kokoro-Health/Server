package health.kokoro.infrastructure.jpa.user.security

import health.kokoro.infrastructure.jpa.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "user_security")
class UserSecurityEntity(
    @Column("password_hash") var passwordHash: String,
    @Column("mfa_enabled") var mfaEnabled: Boolean,
    @Column("mfa_secret") var mfaSecret: String?,
    @Column("verified") var verified: Boolean,
    @Column("verification_code") var verificationCode: String?,
    @Column("verification_code_requested_at") var verificationCodeRequestedAt: Instant?,
    @Column("password_reset_code") var passwordResetCode: String? = null,
    @Column("password_reset_code_requested_at") var passwordResetCodeRequestedAt: Instant? = null
) : BaseEntity()