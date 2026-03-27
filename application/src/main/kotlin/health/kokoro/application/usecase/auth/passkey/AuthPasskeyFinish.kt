package health.kokoro.application.usecase.auth.passkey

import com.yubico.webauthn.FinishAssertionOptions
import com.yubico.webauthn.RelyingParty
import com.yubico.webauthn.AssertionRequest
import com.yubico.webauthn.data.PublicKeyCredential
import health.kokoro.application.security.JwtUtil
import health.kokoro.domain.model.user.security.passkey.ChallengeType
import health.kokoro.domain.port.user.passkey.PasskeyChallengeRepository
import health.kokoro.domain.port.user.passkey.PasskeyRepository
import health.kokoro.domain.port.user.UserRepository
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
            ?: throw IllegalStateException("No active authentication challenge for this email")

        if (challenge.expiresAt.isBefore(Instant.now())) {
            challengeRepository.delete(challenge)
            throw IllegalStateException("Challenge expired")
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
            ?: throw IllegalStateException("Credential not found")

        passkeyRepository.save(
            passkey.copy(
                signCount = result.signatureCount,
                lastUsedAt = Instant.now()
            )
        )

        return jwtUtil.generateToken(email)
    }
}
