package health.kokoro.application.usecase.auth

import dev.samstevens.totp.code.CodeVerifier
import health.kokoro.domain.model.user.User
import health.kokoro.domain.port.user.UserSecurityRepository
import org.springframework.stereotype.Service

@Service
class VerifyMfaTotp(
    private val codeVerifier: CodeVerifier,
    private val securityRepository: UserSecurityRepository
) {
    fun execute(user: User, code: String): Boolean {
        val secret = user.security.mfaSecret
            ?: throw IllegalStateException("MFA is not set up for this user")

        return codeVerifier.isValidCode(secret, code)
    }

    fun executeAndEnable(user: User, code: String) {
        if (user.security.mfaEnabled) {
            throw IllegalStateException("MFA is already enabled")
        }

        if (!execute(user, code)) {
            throw IllegalArgumentException("Invalid verification code")
        }

        securityRepository.enableMfa(user)
    }
}
