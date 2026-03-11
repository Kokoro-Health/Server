package health.kokoro.application.usecase.auth

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
            throw IllegalStateException("MFA is not enabled")
        }

        if (!passwordEncoder.matches(password, user.security.passwordHash)) {
            throw IllegalArgumentException("Invalid password")
        }

        securityRepository.disableMfa(user)
    }
}
