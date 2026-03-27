package health.kokoro.application.usecase.auth.passkey

import com.yubico.webauthn.RelyingParty
import com.yubico.webauthn.StartAssertionOptions
import health.kokoro.domain.model.user.security.passkey.ChallengeType
import health.kokoro.domain.model.user.security.passkey.PasskeyChallenge
import health.kokoro.domain.port.user.passkey.PasskeyChallengeRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class AuthPasskeyStart(
    private val relyingParty: RelyingParty,
    private val challengeRepository: PasskeyChallengeRepository,
) {
    fun executeToJson(email: String): String {
        val options = relyingParty.startAssertion(
            StartAssertionOptions.builder()
                .username(email)
                .build()
        )

        challengeRepository.save(
            PasskeyChallenge(
                id = UUID.randomUUID(),
                userId = null,
                email = email,
                type = ChallengeType.AUTHENTICATION,
                data = options.toCredentialsGetJson(),
                expiresAt = Instant.now().plusSeconds(300),
                createdAt = Instant.now()
            )
        )

        return options.toCredentialsGetJson()
    }
}
