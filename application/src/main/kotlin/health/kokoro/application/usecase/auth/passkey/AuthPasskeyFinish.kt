package health.kokoro.application.usecase.auth.passkey

import com.yubico.webauthn.AssertionRequest
import com.yubico.webauthn.FinishAssertionOptions
import com.yubico.webauthn.RelyingParty
import com.yubico.webauthn.data.PublicKeyCredential
import health.kokoro.application.security.JwtUtil
import health.kokoro.application.usecase.util.RequestDetails
import health.kokoro.domain.error.ChallengeExpiredException
import health.kokoro.domain.error.ChallengeNotFoundException
import health.kokoro.domain.error.CredentialNotFoundException
import health.kokoro.domain.error.UserNotFoundException
import health.kokoro.domain.model.audit.AuditAction
import health.kokoro.domain.model.audit.AuditEvent
import health.kokoro.domain.model.user.User
import health.kokoro.domain.model.user.security.passkey.ChallengeType
import health.kokoro.domain.port.audit.AuditEventRepository
import health.kokoro.domain.port.user.UserRepository
import health.kokoro.domain.port.user.passkey.PasskeyChallengeRepository
import health.kokoro.domain.port.user.passkey.PasskeyRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class AuthPasskeyFinish(
    private val relyingParty: RelyingParty,
    private val challengeRepository: PasskeyChallengeRepository,
    private val passkeyRepository: PasskeyRepository,
    private val jwtUtil: JwtUtil,
    private val auditLog: AuditEventRepository,
    private val userRepo: UserRepository
) {
    fun execute(email: String, credential: String, req: HttpServletRequest): String {
        val challenge = challengeRepository.findByEmailAndType(email, ChallengeType.AUTHENTICATION)
            ?: throw ChallengeNotFoundException("authentication")
        val user = userRepo.findByEmail(email) ?: throw UserNotFoundException()
        if (challenge.expiresAt.isBefore(Instant.now())) {
            challengeRepository.delete(challenge)
            throw ChallengeExpiredException()
        }

        val pkc = PublicKeyCredential.parseAssertionResponseJson(credential)
        val request = AssertionRequest.fromJson(challenge.data)

        val result = relyingParty.finishAssertion(
            FinishAssertionOptions.builder()
                .request(request)
                .response(pkc)
                .build()
        )

        addAuditLog(user, false, req)
        if (!result.isSuccess) throw IllegalStateException("Passkey authentication failed")

        challengeRepository.delete(challenge)

        val passkey = passkeyRepository.findByCredentialId(result.credential.credentialId.base64Url)
            ?: throw CredentialNotFoundException()

        passkeyRepository.save(
            passkey.copy(
                signCount = result.signatureCount,
                lastUsedAt = Instant.now()
            )
        )
        addAuditLog(user, true, req)
        return jwtUtil.generateToken(email)
    }

    fun addAuditLog(user: User, success: Boolean, request: HttpServletRequest) {
        val action: AuditAction = if (success) {
            AuditAction.LOGIN_SUCCESS
        } else {
            AuditAction.LOGIN_FAILED
        }
        val details = RequestDetails(request)
        val meta = mapOf(
            "auth_method" to "passkey"
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
}
