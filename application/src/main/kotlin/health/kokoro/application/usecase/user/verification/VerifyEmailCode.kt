package health.kokoro.application.usecase.user.verification

import health.kokoro.domain.error.CodeExpiredException
import health.kokoro.domain.error.InvalidVerificationCodeException
import health.kokoro.domain.model.user.User
import health.kokoro.domain.port.user.UserSecurityRepository
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class VerifyEmailCode(
    private val userSecurityRepository: UserSecurityRepository,
) {
    fun execute(user: User, code: String) {
        val now = Instant.now()
        val userSecurity = user.security

        val requestedAt = userSecurity.verificationCodeRequestedAt
            ?: throw InvalidVerificationCodeException()

        val expirationTime = requestedAt.plusSeconds(EXPIRATION_DURATION_SECONDS)

        if (now.isAfter(expirationTime)) {
            throw CodeExpiredException()
        }

        if (userSecurity.verificationCode != code) {
            throw InvalidVerificationCodeException()
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