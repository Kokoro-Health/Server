package health.kokoro.application.usecase.auth

import health.kokoro.application.security.JwtUtil
import health.kokoro.domain.error.EmailAlreadyExistsException
import health.kokoro.domain.model.user.User
import health.kokoro.domain.model.user.security.UserSecurity
import health.kokoro.domain.model.user.settings.LanguageSetting
import health.kokoro.domain.model.user.settings.NotificationSettings
import health.kokoro.domain.model.user.settings.Settings
import health.kokoro.domain.model.user.settings.ThemeSetting
import health.kokoro.domain.port.user.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.ZoneId

@Service
class SignUp(
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil,
    private val passwordEncoder: PasswordEncoder
) {
    fun execute(command: Command): Response {
        if (!command.tosAccepted) throw IllegalArgumentException("You must accept the Terms of Service.")
        if (userRepository.existsByEmail(command.email)) throw EmailAlreadyExistsException(command.email)
        val hashedPassword = passwordEncoder.encode(command.password)

        val security = UserSecurity(
            passwordHash = hashedPassword,
            mfaSecret = null,
            mfaEnabled = false,
            verified = false,
            verificationCode = null,
            verificationCodeRequestedAt = null
        )
        val settings = Settings(
            theme = ThemeSetting.LIGHT,
            language = LanguageSetting.ENGLISH,
            dateFormat = "dd-MM-yyyy",
            timeZone = ZoneId.systemDefault(),
            notificationSettings = NotificationSettings(
                marketingEmails = true,
                securityAlerts = true,
                reminderEmails = true
            ),
            updatedAt = Instant.now(),
        )
        var user = User(
            id = null,
            firstName = command.firstName,
            middleName = command.middleName,
            lastName = command.lastName,
            email = command.email,
            security = security,
            settings = settings,
            createdAt = Instant.now(),
            updatedAt = Instant.now(),
            profilePicture = null
        )

        user = userRepository.save(user)
        val token = jwtUtil.generateToken(user.email)
        val expiresIn = jwtUtil.getExpiration(token)

        return Response(token, expiresIn)
    }

    data class Command(
        val firstName: String,
        val middleName: String?,
        val lastName: String,
        val email: String,
        val password: String,
        val tosAccepted: Boolean
    )

    data class Response(
        val token: String, val expiresIn: Long
    )
}