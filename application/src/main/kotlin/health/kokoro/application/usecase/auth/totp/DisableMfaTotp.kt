package health.kokoro.application.usecase.auth.totp

import health.kokoro.domain.error.InvalidPasswordException
import health.kokoro.domain.error.MfaNotEnabledException
import health.kokoro.domain.model.user.User
import health.kokoro.domain.port.user.UserSecurityRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class DisableMfaTotp(
    private val securityRepository: UserSecurityRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun execute(user: User, password: String) {
        if (!user.security.mfaEnabled) {
            throw MfaNotEnabledException()
        }

        if (!passwordEncoder.matches(password, user.security.passwordHash)) {
            throw InvalidPasswordException()
        }

        securityRepository.disableMfa(user)
    }
}