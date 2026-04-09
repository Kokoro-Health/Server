package health.kokoro.application.usecase.user

import health.kokoro.domain.error.EmailAlreadyTakenException
import health.kokoro.domain.model.user.User
import health.kokoro.domain.port.user.UserRepository
import org.springframework.stereotype.Service

@Service
class UpdateProfile(
    private val userRepository: UserRepository
) {
    fun execute(data: Command, user: User) {
        val emailChanged = data.email != user.email

        if (emailChanged) {
            val userWithMail = userRepository.findByEmail(data.email)
            if (userWithMail != null) {
                throw EmailAlreadyTakenException()
            }
        }

        val updatedUser = user.copy(
            firstName = data.firstName,
            middleName = data.middleName,
            lastName = data.lastName,
            email = data.email,
            security = user.security.copy(
                verified = user.security.verified && !emailChanged
            )
        )

        userRepository.save(updatedUser)
    }

    data class Command(
        val firstName: String,
        val middleName: String?,
        val lastName: String,
        val email: String
    )
}
