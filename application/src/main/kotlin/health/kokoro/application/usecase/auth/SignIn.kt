package health.kokoro.application.usecase.auth

import health.kokoro.application.security.JwtUtil
import health.kokoro.domain.port.user.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service

@Service
class SignIn(
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil,
    private val authenticationManager: AuthenticationManager,
    private val verifyMfaTotp: VerifyMfaTotp
) {
    fun execute(command: Command): Response {
        val user = userRepository.findByEmail(command.email)
            ?: throw IllegalArgumentException("Invalid credentials")

        val auth = UsernamePasswordAuthenticationToken(command.email, command.password)
        authenticationManager.authenticate(auth)

        if (user.security.mfaEnabled) {
            if (command.mfaCode.isNullOrBlank()) {
                return Response(mfaRequired = true)
            }

            if (!verifyMfaTotp.execute(user, command.mfaCode)) {
                throw IllegalArgumentException("Invalid MFA code")
            }
        }

        val token = jwtUtil.generateToken(command.email)
        val expiresIn = jwtUtil.getExpiration(token)

        return Response(
            mfaRequired = false,
            token = token,
            expiresIn = expiresIn
        )
    }

    data class Command(
        val email: String,
        val password: String,
        val mfaCode: String? = null
    )

    data class Response(
        val mfaRequired: Boolean,
        val token: String? = null,
        val expiresIn: Long? = null
    )
}