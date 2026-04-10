package health.kokoro.application.usecase.auth.totp

import dev.samstevens.totp.code.CodeVerifier
import health.kokoro.application.usecase.util.RequestDetails
import health.kokoro.domain.error.InvalidMfaCodeException
import health.kokoro.domain.error.MfaAlreadyEnabledException
import health.kokoro.domain.error.MfaNotSetupException
import health.kokoro.domain.model.audit.AuditAction
import health.kokoro.domain.model.audit.AuditEvent
import health.kokoro.domain.model.user.User
import health.kokoro.domain.port.audit.AuditEventRepository
import health.kokoro.domain.port.user.UserSecurityRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class VerifyMfaTotp(
    private val codeVerifier: CodeVerifier,
    private val securityRepository: UserSecurityRepository,
    private val auditLog: AuditEventRepository
) {
    fun execute(user: User, code: String): Boolean {
        val secret = user.security.mfaSecret
            ?: throw MfaNotSetupException()

        return codeVerifier.isValidCode(secret, code)
    }

    fun executeAndEnable(user: User, code: String, req: HttpServletRequest) {
        if (user.security.mfaEnabled) {
            throw MfaAlreadyEnabledException()
        }

        if (!execute(user, code)) {
            throw InvalidMfaCodeException()
        }
        addAuditLog(user, req)
        securityRepository.enableMfa(user)
    }

    fun addAuditLog(user: User, request: HttpServletRequest) {
        val details = RequestDetails(request)
        val event = AuditEvent(
            id = UUID.randomUUID(),
            userId = user.id!!,
            action = AuditAction.MFA_ENABLED,
            userAgent = details.getUserAgent(),
            ipAddress = details.getIpAddress(),
            metaData = null,
            timeStamp = Instant.now()
        )
        auditLog.add(event)
    }
}