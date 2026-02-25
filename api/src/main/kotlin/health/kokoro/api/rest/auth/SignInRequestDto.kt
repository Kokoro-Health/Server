package health.kokoro.api.rest.auth

import jakarta.validation.constraints.Email

data class SignInRequestDto(
    @param:Email val email: String,
    val password: String
)