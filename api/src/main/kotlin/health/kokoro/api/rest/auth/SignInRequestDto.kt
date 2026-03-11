package health.kokoro.api.rest.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

data class SignInRequestDto(
    @param:Email val email: String,
    val password: String,
    val rememberMe: Boolean,
    @field:Size
    val mfaCode: String? = null
)