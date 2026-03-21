package health.kokoro.application.usecase.user.verification

import health.kokoro.domain.model.user.User
import health.kokoro.domain.port.user.UserSecurityRepository
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Instant

@Service
class VerifyEmailCode(
    private val userSecurityRepository: UserSecurityRepository,
) {
    fun execute(user: User, code: String) {
        val now = Instant.now()
        val userSecurity = user.security

        val requestedAt = userSecurity.verificationCodeRequestedAt
            ?: throw IllegalStateException("Verification code not requested")

        val expirationTime = requestedAt.plusSeconds(EXPIRATION_DURATION_SECONDS)

        if (now.isAfter(expirationTime)) {
            throw IllegalStateException("Verification code expired")
        }

        if (userSecurity.verificationCode != code) {
            throw IllegalArgumentException("Invalid verification code")
        }

        val updatedSecurity = userSecurity.copy(
            verified = true,
            verificationCode = null,
            verificationCodeRequestedAt = null
        )

        userSecurityRepository.save(updatedSecurity)
    }

    companion object {
        private const val EXPIRATION_DURATION_SECONDS = 900L
    }
}