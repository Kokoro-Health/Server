package health.kokoro.api.rest.auth

import health.kokoro.api.validation.Password
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

data class PasswordResetRequestDto(
    val code: String,
    @field:Password
    val password: String,
)

data class SignInRequestDto(
    @field:Email val email: String,
    val password: String,
    val rememberMe: Boolean,
    @field:Size @field:Schema(nullable = true)
    val mfaCode: String? = null
)

data class SignInResponseDto(
    val mfaRequired: Boolean
)

data class AuthTokenResponseDto(
    val token: String,
    val expiresIn: Long
)

data class SignUpRequestDto(
    @field:Size(min = 2, max = 20)
    val firstName: String,
    @field:Size(min = 0, max = 20)
    val middleName: String?,
    @field:Size(min = 2, max = 20)
    val lastName: String,
    @field:Email
    val email: String,
    @field:Password
    val password: String,
    val tosAccepted: Boolean
)
