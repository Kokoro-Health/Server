package health.kokoro.application.usecase.auth.passkey

import com.yubico.webauthn.FinishRegistrationOptions
import com.yubico.webauthn.RelyingParty
import com.yubico.webauthn.data.PublicKeyCredential
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions
import health.kokoro.domain.error.ChallengeExpiredException
import health.kokoro.domain.error.ChallengeNotFoundException
import health.kokoro.domain.model.user.User
import health.kokoro.domain.model.user.security.passkey.ChallengeType
import health.kokoro.domain.model.user.security.passkey.Passkey
import health.kokoro.domain.port.user.passkey.PasskeyChallengeRepository
import health.kokoro.domain.port.user.passkey.PasskeyRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class RegisterPasskeyFinish(
    private val relyingParty: RelyingParty,
    private val challengeRepository: PasskeyChallengeRepository,
    private val passkeyRepository: PasskeyRepository,
) {
    fun execute(user: User, credential: String, deviceName: String): Passkey {
        val challenge = challengeRepository.findByUserIdAndType(user.id!!, ChallengeType.REGISTRATION)
            ?: throw ChallengeNotFoundException("registration")

        if (challenge.expiresAt.isBefore(Instant.now())) {
            challengeRepository.delete(challenge)
            throw ChallengeExpiredException()
        }

        val pkc = PublicKeyCredential.parseRegistrationResponseJson(credential)
        val request = PublicKeyCredentialCreationOptions.fromJson(challenge.data)

        val result = relyingParty.finishRegistration(
            FinishRegistrationOptions.builder()
                .request(request)
                .response(pkc)
                .build()
        )

        challengeRepository.delete(challenge)

        return passkeyRepository.save(
            Passkey(
                id = UUID.randomUUID(),
                userId = user.id!!,
                credentialId = result.keyId.id.base64Url,
                publicKey = result.publicKeyCose.bytes,
                signCount = result.signatureCount,
                deviceName = deviceName,
                createdAt = Instant.now(),
                lastUsedAt = Instant.now()
            )
        )
    }
}
