package health.kokoro.api.rest.auth

data class AuthResponseDto(
    val token: String,
    val expiresIn: Long
)