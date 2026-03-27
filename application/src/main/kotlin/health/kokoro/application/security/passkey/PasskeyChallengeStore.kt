package health.kokoro.application.security.passkey

import com.fasterxml.jackson.databind.ObjectMapper
import com.yubico.webauthn.AssertionRequest
import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions
import health.kokoro.domain.model.user.security.passkey.ChallengeType
import health.kokoro.domain.model.user.security.passkey.PasskeyChallenge
import health.kokoro.domain.port.user.passkey.PasskeyChallengeRepository
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

@Component
class PasskeyChallengeStore(
    private val repository: PasskeyChallengeRepository,
) {
    fun storeRegistration(userId: UUID, options: PublicKeyCredentialCreationOptions) {
        repository.deleteByUserId(userId)
        repository.save(
            PasskeyChallenge(
                id = UUID.randomUUID(),
                userId = userId,
                type = ChallengeType.REGISTRATION,
                data = options.toCredentialsCreateJson(),
                expiresAt = Instant.now().plus(5, ChronoUnit.MINUTES),
                createdAt = Instant.now(),
                email = null
            )
        )
    }

    fun getRegistration(userId: UUID): PublicKeyCredentialCreationOptions? {
        val challenge = repository.findByUserIdAndType(userId, ChallengeType.REGISTRATION) ?: return null
        if (challenge.expiresAt.isBefore(Instant.now())) {
            repository.deleteByUserId(userId)
            return null
        }
        return PublicKeyCredentialCreationOptions.fromJson(challenge.data)
    }

    fun storeAssertion(email: String, options: AssertionRequest) {
        repository.deleteByEmail(email)
        repository.save(PasskeyChallenge(
            email = email,
            type = ChallengeType.AUTHENTICATION,
            data = options.toCredentialsGetJson(),
            expiresAt = Instant.now().plus(5, ChronoUnit.MINUTES),
            id = UUID.randomUUID(),
            userId = null,
            createdAt = Instant.now()
        ))
    }

    fun getAssertion(email: String): AssertionRequest? {
        val challenge = repository.findByEmailAndType(email, ChallengeType.AUTHENTICATION) ?: return null
        if (challenge.expiresAt.isBefore(Instant.now())) {
            repository.deleteByEmail(email)
            return null
        }
        return AssertionRequest.fromJson(challenge.data)
    }

    fun evictByUserId(userId: UUID) = repository.deleteByUserId(userId)
    fun evictByEmail(email: String) = repository.deleteByEmail(email)
}