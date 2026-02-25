package health.kokoro.application.usecase.auth

import health.kokoro.application.security.JwtUtil
import health.kokoro.domain.model.User
import health.kokoro.domain.port.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Instant

@Service
class SignUp(
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil,
    private val passwordEncoder: PasswordEncoder,
    private val clock: Clock
) {
    fun execute(command: Command): AuthResponse {
        if (userRepository.existsByEmail(command.email)) throw IllegalArgumentException("This email is already in use.")
        val hashedPassword = passwordEncoder.encode(command.password)

        var user = User(
            id = null,
            firstName = command.firstName,
            middleName = command.middleName,
            lastName = command.lastName,
            email = command.email,
            passwordHash = hashedPassword,
            profilePictureUrl = null,
            createdAt = Instant.now(clock),
            updatedAt = Instant.now(clock)
        )

        user = userRepository.save(user)
        val token = jwtUtil.generateToken(user.email)
        val expiresIn = jwtUtil.getExpiration(token)

        return AuthResponse(token, expiresIn)
    }

    data class Command(
        val firstName: String, val middleName: String?, val lastName: String, val email: String, val password: String
    )
}