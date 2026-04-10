package health.kokoro.application.usecase.auth

import health.kokoro.application.security.JwtUtil
import health.kokoro.application.usecase.auth.totp.VerifyMfaTotp
import health.kokoro.application.usecase.util.RequestDetails
import health.kokoro.domain.error.InvalidMfaCodeException
import health.kokoro.domain.error.UserNotFoundException
import health.kokoro.domain.model.audit.AuditAction
import health.kokoro.domain.model.audit.AuditEvent
import health.kokoro.domain.model.user.User
import health.kokoro.domain.port.audit.AuditEventRepository
import health.kokoro.domain.port.user.UserRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class SignIn(
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil,
    private val authenticationManager: AuthenticationManager,
    private val verifyMfaTotp: VerifyMfaTotp,
    private val auditLog: AuditEventRepository
) {
    fun execute(command: Command, request: HttpServletRequest): Response {
        val user = userRepository.findByEmail(command.email)
            ?: throw UserNotFoundException()

        val auth = UsernamePasswordAuthenticationToken(command.email, command.password)

        try {
            authenticationManager.authenticate(auth)
        } catch (_: Exception) {
            addAuditLog(user, false, request)
        }

        if (user.security.mfaEnabled) {
            if (command.mfaCode.isNullOrBlank()) {
                return Response(mfaRequired = true)
            }

            if (!verifyMfaTotp.execute(user, command.mfaCode)) {
                addAuditLog(user, false, request)
                throw InvalidMfaCodeException()
            }
        }

        val token = jwtUtil.generateToken(command.email)
        val expiresIn = jwtUtil.getExpiration(token)
        addAuditLog(user, true, request)
        return Response(
            mfaRequired = false,
            token = token,
            expiresIn = expiresIn
        )
    }

    fun addAuditLog(user: User, success: Boolean, request: HttpServletRequest) {
        val action: AuditAction = if (success) {
            AuditAction.LOGIN_SUCCESS
        } else {
            AuditAction.LOGIN_FAILED
        }
        val details = RequestDetails(request)
        val meta = mapOf(
            "auth_method" to "credentials"
        )
        val event = AuditEvent(
            id = UUID.randomUUID(),
            userId = user.id!!,
            action = action,
            userAgent = details.getUserAgent(),
            ipAddress = details.getIpAddress(),
            metaData = meta,
            timeStamp = Instant.now()
        )
        auditLog.add(event)
    }

    data class Command(
        val email: String,
        val password: String,
        val mfaCode: String? = null
    )

    data class Response(
        val mfaRequired: Boolean,
        val token: String? = null,
        val expiresIn: Long? = null
    )
}