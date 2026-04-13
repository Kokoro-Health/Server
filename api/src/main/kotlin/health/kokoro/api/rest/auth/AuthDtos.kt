package health.kokoro.api.rest.auth

import health.kokoro.api.validation.Password
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

data class PasswordResetRequestDto(
    @field:Schema(description = "Reset code from email", example = "123456")
    val code: String,
    @field:Password
    @field:Schema(description = "New password (min 8 chars, uppercase, lowercase, digit, special char)", example = "SecurePass123!")
    val password: String,
)

data class SignInRequestDto(
    @field:Email(message = "Invalid email format")
    @field:Schema(description = "Email address", example = "john.doe@example.com")
    val email: String,
    @field:Schema(description = "Account password", example = "SecurePass123!")
    val password: String,
    @field:Schema(description = "Remember me for 30 days")
    val rememberMe: Boolean,
    @field:Size(min = 6, max = 6)
    @field:Schema(description = "6-digit MFA code (if enabled)", example = "123456", nullable = true)
    val mfaCode: String? = null
)

data class SignInResponseDto(
    @field:Schema(description = "Whether MFA verification is required")
    val mfaRequired: Boolean
)

data class AuthTokenResponseDto(
    @field:Schema(description = "JWT access token")
    val token: String,
    @field:Schema(description = "Token expiration time in seconds")
    val expiresIn: Long
)

data class SignUpRequestDto(
    @field:Size(min = 2, max = 20, message = "First name must be 2-20 characters")
    @field:Schema(description = "First name", example = "John")
    val firstName: String,
    @field:Size(max = 20, message = "Middle name must be max 20 characters")
    @field:Schema(description = "Middle name", example = "Michael", nullable = true)
    val middleName: String?,
    @field:Size(min = 2, max = 20, message = "Last name must be 2-20 characters")
    @field:Schema(description = "Last name", example = "Doe")
    val lastName: String,
    @field:Email(message = "Invalid email format")
    @field:Schema(description = "Email address", example = "john.doe@example.com")
    val email: String,
    @field:Password
    @field:Schema(description = "Account password (min 8 chars, uppercase, lowercase, digit, special char)", example = "SecurePass123!")
    val password: String,
    @field:Schema(description = "Terms of service acceptance")
    val tosAccepted: Boolean
)
