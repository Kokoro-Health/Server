package health.kokoro.application.auth

import org.springframework.stereotype.Service

@Service
class SignIn {
    fun execute(command: Command): AuthResponse {
        return AuthResponse("", 0)
    }

    data class Command(val email: String, val password: String)
}