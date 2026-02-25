package health.kokoro.application.usecase.auth

data class AuthResponse(
    val token: String, val expiresIn: Long
)