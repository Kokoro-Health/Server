package health.kokoro.application.auth

import org.springframework.stereotype.Service

@Service
class SignUp {
    fun execute(command: Command): AuthResponse {
       return AuthResponse("", 0)
    }

    data class Command(
        val firstName: String, val middleName: String?, val lastName: String, val email: String, val password: String
    )
}