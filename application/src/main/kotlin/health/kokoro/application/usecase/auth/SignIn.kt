package health.kokoro.application.usecase.auth

import health.kokoro.application.security.JwtUtil
import health.kokoro.domain.port.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service

@Service
class SignIn(
    private val userRepository: UserRepository,
    private val jwtUtil: JwtUtil,
    private val authenticationManager: AuthenticationManager
) {
    fun execute(command: Command): AuthResponse {
       if (!userRepository.existsByEmail(command.email)) {
           throw IllegalArgumentException("User not found")
       }

        val auth = UsernamePasswordAuthenticationToken(command.email, command.password)
        authenticationManager.authenticate(auth)

        val token = jwtUtil.generateToken(command.email)
        val expiresIn = jwtUtil.getExpiration(token)

        return AuthResponse(token, expiresIn)
    }

    data class Command(val email: String, val password: String)
}