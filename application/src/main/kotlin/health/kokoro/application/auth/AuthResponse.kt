package health.kokoro.application.auth

data class AuthResponse(
    val token: String, val expiresIn: Long
)