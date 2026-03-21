package health.kokoro.application.usecase.user.verification

import health.kokoro.domain.model.user.User
import health.kokoro.domain.port.mail.MailSenderRepository
import health.kokoro.domain.port.user.UserSecurityRepository
import org.springframework.stereotype.Service
import java.security.SecureRandom
import java.time.Duration
import java.time.Instant

@Service
class RequestVerificationCode(
    private val secureRandom: SecureRandom,
    private val userSecurityRepository: UserSecurityRepository,
    private val emailRepository: MailSenderRepository
) {
    fun execute(user: User): Response {
        require(!user.security.verified) { "User already verified" }

        val now = Instant.now()
        val lastRequestTime = user.security.verificationCodeRequestedAt

        if (lastRequestTime != null) {
            val elapsed = Duration.between(lastRequestTime, now).seconds
            if (elapsed < COOLDOWN_SECONDS) {
                val remaining = COOLDOWN_SECONDS - elapsed
                return Response(now.plusSeconds(remaining))
            }
        }

        val newCode = generateCode()
        userSecurityRepository.save(
            user.security.copy(
                verificationCode = newCode,
                verificationCodeRequestedAt = now
            )
        )

        val model = mapOf(
            "email" to user.email,
            "code" to newCode,
            "expirationMinutes" to 15,
            "year" to 2026
        )

        emailRepository.sendTemplate(user.email, "Verify your account.", "verification-email", model)
        return Response(now)
    }

    private fun generateCode(): String {
        val digits = CharArray(CODE_LENGTH) { _ ->
            secureRandom.nextInt(DIGIT_RANGE_START, DIGIT_RANGE_END + 1).toString()[0]
        }
        return digits.concatToString()
    }

    companion object {
        private const val CODE_LENGTH = 6
        private const val COOLDOWN_SECONDS = 60L
        private const val DIGIT_RANGE_START = 0
        private const val DIGIT_RANGE_END = 9
    }

    data class Response(val nextAllowedAt: Instant)
}