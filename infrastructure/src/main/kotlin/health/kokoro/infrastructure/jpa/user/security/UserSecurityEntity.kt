package health.kokoro.infrastructure.jpa.user.security

import health.kokoro.infrastructure.jpa.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "user_security")
class UserSecurityEntity(
    @Column("password_hash") var passwordHash: String,
    @Column("mfa_enabled") var mfaEnabled: Boolean,
    @Column("mfa_secret") var mfaSecret: String?
) : BaseEntity()