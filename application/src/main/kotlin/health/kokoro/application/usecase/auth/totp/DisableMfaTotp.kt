package health.kokoro.application.usecase.auth.totp

import health.kokoro.application.usecase.util.RequestDetails
import health.kokoro.domain.error.InvalidPasswordException
import health.kokoro.domain.error.MfaNotEnabledException
import health.kokoro.domain.model.audit.AuditAction
import health.kokoro.domain.model.audit.AuditEvent
import health.kokoro.domain.model.user.User
import health.kokoro.domain.port.audit.AuditEventRepository
import health.kokoro.domain.port.user.UserSecurityRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class DisableMfaTotp(
    private val securityRepository: UserSecurityRepository,
    private val passwordEncoder: PasswordEncoder,
    private val auditLog: AuditEventRepository
) {
    fun execute(user: User, password: String, req: HttpServletRequest) {
        if (!user.security.mfaEnabled) {
            throw MfaNotEnabledException()
        }

        if (!passwordEncoder.matches(password, user.security.passwordHash)) {
            throw InvalidPasswordException()
        }
        addAuditLog(user, req)
        securityRepository.disableMfa(user)
    }

    fun addAuditLog(user: User, request: HttpServletRequest) {
        val details = RequestDetails(request)
        val event = AuditEvent(
            id = UUID.randomUUID(),
            userId = user.id!!,
            action = AuditAction.MFA_DISABLED,
            userAgent = details.getUserAgent(),
            ipAddress = details.getIpAddress(),
            metaData = null,
            timeStamp = Instant.now()
        )
        auditLog.add(event)
    }
}