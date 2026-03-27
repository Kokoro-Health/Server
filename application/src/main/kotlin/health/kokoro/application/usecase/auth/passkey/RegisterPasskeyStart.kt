package health.kokoro.application.usecase.auth.passkey

import com.yubico.webauthn.RelyingParty
import com.yubico.webauthn.StartRegistrationOptions
import com.yubico.webauthn.data.ByteArray
import com.yubico.webauthn.data.UserIdentity
import health.kokoro.domain.model.user.User
import health.kokoro.domain.model.user.security.passkey.ChallengeType
import health.kokoro.domain.model.user.security.passkey.PasskeyChallenge
import health.kokoro.domain.port.user.passkey.PasskeyChallengeRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class RegisterPasskeyStart(
    private val relyingParty: RelyingParty,
    private val challengeRepository: PasskeyChallengeRepository,
) {
        fun executeToJson(user: User): String {
        var displayName = user.firstName + " "
            if (user.middleName != null) {
                displayName += user.middleName + " "
            }
            displayName += user.lastName
        val options = relyingParty.startRegistration(
            StartRegistrationOptions.builder()
                .user(
                    UserIdentity.builder()
                        .name(user.email)
                        .displayName(displayName)
                        .id(ByteArray.fromHex(user.id.toString().replace("-", "")))
                        .build()
                )
                .build()
        )

        challengeRepository.save(
            PasskeyChallenge(
                id = UUID.randomUUID(),
                userId = user.id,
                type = ChallengeType.REGISTRATION,
                data = options.toCredentialsCreateJson(),
                expiresAt = Instant.now().plusSeconds(300),
                email = null,
                createdAt = Instant.now()
            )
        )

        return options.toCredentialsCreateJson()
    }
}