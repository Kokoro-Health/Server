package health.kokoro.application.usecase.auth.passkey

import com.yubico.webauthn.AssertionRequest
import com.yubico.webauthn.FinishAssertionOptions
import com.yubico.webauthn.RelyingParty
import com.yubico.webauthn.data.PublicKeyCredential
import health.kokoro.application.security.JwtUtil
import health.kokoro.domain.error.ChallengeExpiredException
import health.kokoro.domain.error.ChallengeNotFoundException
import health.kokoro.domain.error.CredentialNotFoundException
import health.kokoro.domain.model.user.security.passkey.ChallengeType
import health.kokoro.domain.port.user.passkey.PasskeyChallengeRepository
import health.kokoro.domain.port.user.passkey.PasskeyRepository
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class AuthPasskeyFinish(
    private val relyingParty: RelyingParty,
    private val challengeRepository: PasskeyChallengeRepository,
    private val passkeyRepository: PasskeyRepository,
    private val jwtUtil: JwtUtil,
) {
    fun execute(email: String, credential: String): String {
        val challenge = challengeRepository.findByEmailAndType(email, ChallengeType.AUTHENTICATION)
            ?: throw ChallengeNotFoundException("authentication")

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

        return jwtUtil.generateToken(email)
    }
}
