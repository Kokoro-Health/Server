package health.kokoro.infrastructure.jpa.user.security

import health.kokoro.domain.model.user.security.UserSecurity
import org.springframework.stereotype.Component

@Component
class UserSecurityMapper {
    fun toEntity(domain: UserSecurity): UserSecurityEntity {
        val entity = UserSecurityEntity(
            passwordHash = domain.passwordHash,
            mfaEnabled = domain.mfaEnabled,
            mfaSecret = domain.mfaSecret
        )
        entity.id = domain.id
        return entity
    }

    fun toDomain(entity: UserSecurityEntity): UserSecurity {
        return UserSecurity(
            id = entity.id,
            passwordHash = entity.passwordHash,
            mfaEnabled = entity.mfaEnabled,
            mfaSecret = entity.mfaSecret
        )
    }
}