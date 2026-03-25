package health.kokoro.infrastructure.jpa.user.security

import health.kokoro.domain.model.user.security.UserSecurity
import health.kokoro.domain.port.security.EncryptionPort
import org.springframework.stereotype.Component

@Component
class UserSecurityMapper(
    private val encryptionPort: EncryptionPort
) {
    fun toEntity(domain: UserSecurity): UserSecurityEntity {
        val entity = UserSecurityEntity(
            passwordHash = encryptionPort.encrypt(domain.passwordHash),
            mfaEnabled = domain.mfaEnabled,
            mfaSecret = domain.mfaSecret?.let { encryptionPort.encrypt(it) },
            verified = domain.verified,
            verificationCode = domain.verificationCode?.let { encryptionPort.encrypt(it) },
            verificationCodeRequestedAt = domain.verificationCodeRequestedAt,
            passwordResetCode = domain.passwordResetCode,
            passwordResetCodeRequestedAt = domain.passwordResetCodeRequestedAt
        )
        entity.id = domain.id
        return entity
    }

    fun toDomain(entity: UserSecurityEntity): UserSecurity {
        return UserSecurity(
            id = entity.id,
            passwordHash = encryptionPort.decrypt(entity.passwordHash),
            mfaEnabled = entity.mfaEnabled,
            mfaSecret = entity.mfaSecret?.let { encryptionPort.decrypt(it) },
            verified = entity.verified,
            verificationCode = entity.verificationCode?.let { encryptionPort.decrypt(it) },
            verificationCodeRequestedAt = entity.verificationCodeRequestedAt,
            passwordResetCode = entity.passwordResetCode,
            passwordResetCodeRequestedAt = entity.passwordResetCodeRequestedAt
        )
    }
}