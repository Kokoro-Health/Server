package health.kokoro.infrastructure.jpa.user

import health.kokoro.domain.model.user.User
import health.kokoro.infrastructure.jpa.user.security.UserSecurityMapper
import health.kokoro.infrastructure.jpa.user.settings.SettingsMapper
import org.springframework.stereotype.Component

@Component
class UserMapper(
    private val securityMapper: UserSecurityMapper,
    private val settingsMapper: SettingsMapper
) {
    fun toDomain(user: UserEntity): User {
        return User(
            id = user.id!!,
            firstName = user.firstName,
            middleName = user.middleName,
            lastName = user.lastName,
            email = user.email,
            profilePictureUrl = user.profilePictureUrl,
            security = securityMapper.toDomain(user.security),
            settings = settingsMapper.toDomain(user.settings),
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        )
    }

    fun toEntity(user: User): UserEntity {
        val userEntity = UserEntity(
            firstName = user.firstName,
            middleName = user.middleName,
            lastName = user.lastName,
            email = user.email,
            profilePictureUrl = user.profilePictureUrl,
            security = securityMapper.toEntity(user.security),
            settings = settingsMapper.toEntity(user.settings)
        )
        userEntity.id = user.id
        userEntity.createdAt = user.createdAt
        userEntity.updatedAt = user.updatedAt
        return userEntity
    }
}