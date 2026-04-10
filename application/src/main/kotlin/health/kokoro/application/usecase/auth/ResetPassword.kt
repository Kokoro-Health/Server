package health.kokoro.application.usecase.auth

import health.kokoro.application.usecase.util.CodeGenerator
import health.kokoro.domain.error.*
import health.kokoro.domain.port.mail.MailSenderRepository
import health.kokoro.domain.port.user.UserRepository
import health.kokoro.domain.port.user.UserSecurityRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant

@Service
class ResetPassword(
    private val userSecurityRepository: UserSecurityRepository,
    private val userRepository: UserRepository,
    private val codeGenerator: CodeGenerator,
    private val passwordEncoder: PasswordEncoder,
    private val mailSender: MailSenderRepository
) {
    fun execute(email: String) {
        val user = userRepository.findByEmail(email) ?: throw UserNotFoundException()
        val security = user.security

        val code = codeGenerator.generate6Digit()

        mailSender.sendTemplate(
            to = user.email,
            template = "reset-password",
            subject = "Reset your password",
            model = mapOf(
                "code" to code,
                "expirationMinutes" to EXPIRATION_MINUTES,
                "year" to 2026,
                "email" to user.email
            )
        )

        val newSecurity = security.copy(passwordResetCode = code, passwordResetCodeRequestedAt = Instant.now())
        userSecurityRepository.save(newSecurity)
    }

    fun execute(code: String, password: String) {
        val security = userSecurityRepository.findByPasswordResetCode(code)
            ?: throw ChallengeNotFoundException("Password Reset")

        val now = Instant.now()
        val requestedAt = security.passwordResetCodeRequestedAt
            ?: throw ChallengeNotFoundException("Password Reset")

        if (Duration.between(requestedAt, now).toMinutes() > EXPIRATION_MINUTES) {
            throw CodeExpiredException()
        }

        if (passwordEncoder.matches(password, security.passwordHash)) {
            throw SamePasswordException()
        }


        val updatedSecurity = security.copy(
            passwordHash = passwordEncoder.encode(password),
            passwordResetCode = null,
            passwordResetCodeRequestedAt = null
        )
        userSecurityRepository.save(updatedSecurity)
    }

    fun validateCode(code: String) {
        userSecurityRepository.findByPasswordResetCode(code) ?: throw InvalidVerificationCodeException()
    }

    companion object {
        const val EXPIRATION_MINUTES = 15L
    }
}