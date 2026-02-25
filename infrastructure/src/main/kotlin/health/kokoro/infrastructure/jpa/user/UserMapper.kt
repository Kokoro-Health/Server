package health.kokoro.infrastructure.jpa.user

import health.kokoro.domain.model.User
import org.springframework.stereotype.Component

@Component
class UserMapper {
    fun toDomain(user: UserEntity): User {
        return User(
            id = user.id!!,
            firstName = user.firstName,
            middleName = user.middleName,
            lastName = user.lastName,
            email = user.email,
            profilePictureUrl = user.profilePictureUrl,
            passwordHash = user.passwordHash
        )
    }
}