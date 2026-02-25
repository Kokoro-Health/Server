package health.kokoro.api.rest.auth

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size

data class SignUpRequestDto(
    @field:Size(min = 2, max = 20)
    val firstName: String,
    @field:Size(min = 0, max = 20)
    val middleName: String?,
    @field:Size(min = 2, max = 20)
    val lastName: String,
    @field:Email("Invalid email format.")
    val email: String,
    @field:Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    val password: String
)