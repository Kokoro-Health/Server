package health.kokoro.application.usecase.auth.totp

import dev.samstevens.totp.code.CodeVerifier
import health.kokoro.domain.error.InvalidMfaCodeException
import health.kokoro.domain.error.MfaAlreadyEnabledException
import health.kokoro.domain.error.MfaNotSetupException
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
            ?: throw MfaNotSetupException()

        return codeVerifier.isValidCode(secret, code)
    }

    fun executeAndEnable(user: User, code: String) {
        if (user.security.mfaEnabled) {
            throw MfaAlreadyEnabledException()
        }

        if (!execute(user, code)) {
            throw InvalidMfaCodeException()
        }

        securityRepository.enableMfa(user)
    }
}