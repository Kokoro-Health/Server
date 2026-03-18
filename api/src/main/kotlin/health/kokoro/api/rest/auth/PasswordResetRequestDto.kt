package health.kokoro.api.rest.auth

data class PasswordResetRequestDto(
    val code: String,
    val password: String,
)