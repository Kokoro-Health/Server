package health.kokoro.application.usecase.auth

import health.kokoro.domain.port.mail.MailSenderRepository
import health.kokoro.domain.port.user.UserRepository
import health.kokoro.domain.port.user.UserSecurityRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.security.SecureRandom
import java.time.Clock
import java.time.Duration
import java.time.Instant

@Service
class ResetPassword(
    private val userSecurityRepository: UserSecurityRepository,
    private val userRepository: UserRepository,
    private val secureRandom: SecureRandom,
    private val passwordEncoder: PasswordEncoder,
    private val clock: Clock,
    private val mailSender: MailSenderRepository
) {
    fun execute(email: String) {
        val user = userRepository.findByEmail(email) ?: throw IllegalArgumentException("User not found")
        val security = user.security

        val code = generateCode()

        mailSender.sendTemplate(
            to = user.email,
            template = "reset-password",
            subject = "Reset your password",
            model = mapOf("code" to code,
                 "expirationMinutes" to EXPIRATION_MINUTES,
                "year" to 2026,
                "email" to user.email)
        )

        val newSecurity = security.copy(passwordResetCode = code, passwordResetCodeRequestedAt = Instant.now(clock))
        userSecurityRepository.save(newSecurity)
    }

    fun execute(code: String, password: String) {
        val security = userSecurityRepository.findByPasswordResetCode(code)
            ?: throw IllegalArgumentException("Invalid code")

        val now = Instant.now(clock)
        val requestedAt = security.passwordResetCodeRequestedAt
            ?: throw IllegalArgumentException("Code expired or invalid")

        if (Duration.between(requestedAt, now).toMinutes() > EXPIRATION_MINUTES) {
            throw IllegalArgumentException("Code expired")
        }

        val updatedSecurity = security.copy(
            passwordHash = passwordEncoder.encode(password),
            passwordResetCode = null,
            passwordResetCodeRequestedAt = null
        )
        userSecurityRepository.save(updatedSecurity)
    }

    fun validateCode(code: String) {
         userSecurityRepository.findByPasswordResetCode(code) ?: throw IllegalArgumentException("Invalid code")
    }

    private fun generateCode(): String {
        val number = secureRandom.nextInt(1_000_000)
        return "%06d".format(number)
    }

    companion object {
        const val EXPIRATION_MINUTES = 15L
    }
}