package health.kokoro.application.usecase.auth

import health.kokoro.application.usecase.util.CodeGenerator
import health.kokoro.application.usecase.util.RequestDetails
import health.kokoro.domain.error.*
import health.kokoro.domain.model.audit.AuditAction
import health.kokoro.domain.model.audit.AuditEvent
import health.kokoro.domain.model.user.User
import health.kokoro.domain.port.audit.AuditEventRepository
import health.kokoro.domain.port.mail.MailSenderRepository
import health.kokoro.domain.port.user.UserRepository
import health.kokoro.domain.port.user.UserSecurityRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.Instant
import java.util.*

@Service
class ResetPassword(
    private val userSecurityRepository: UserSecurityRepository,
    private val userRepository: UserRepository,
    private val codeGenerator: CodeGenerator,
    private val passwordEncoder: PasswordEncoder,
    private val mailSender: MailSenderRepository,
    private val auditLog: AuditEventRepository
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

    fun execute(user: User, code: String, password: String, req: HttpServletRequest) {
        val security = userSecurityRepository.findByPasswordResetCode(code)
            ?: throw ChallengeNotFoundException("Password Reset")
        if (user.security.id != security.id) throw ChallengeNotFoundException("Password Reset")
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
        addAuditLog(user, req)
        userSecurityRepository.save(updatedSecurity)
    }

    fun validateCode(code: String) {
        userSecurityRepository.findByPasswordResetCode(code) ?: throw InvalidVerificationCodeException()
    }

    fun addAuditLog(user: User, request: HttpServletRequest) {
        val details = RequestDetails(request)
        val event = AuditEvent(
            id = UUID.randomUUID(),
            userId = user.id!!,
            action = AuditAction.PASSWORD_CHANGE,
            userAgent = details.getUserAgent(),
            ipAddress = details.getIpAddress(),
            metaData = null,
            timeStamp = Instant.now()
        )
        auditLog.add(event)
    }

    companion object {
        const val EXPIRATION_MINUTES = 15L
    }
}