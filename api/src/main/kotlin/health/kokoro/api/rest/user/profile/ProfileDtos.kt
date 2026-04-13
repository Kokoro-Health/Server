package health.kokoro.api.rest.user.profile

import health.kokoro.domain.model.user.settings.ThemeSetting
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Size
import java.time.Instant

data class ProfileResponseDto(
    @field:Schema(description = "User ID", example = "550e8400-e29b-41d4-a716-446655440000")
    val id: String,
    @field:Schema(description = "First name", example = "John")
    val firstName: String,
    @field:Schema(description = "Middle name", example = "Michael", nullable = true)
    val middleName: String?,
    @field:Schema(description = "Last name", example = "Doe")
    val lastName: String,
    @field:Schema(description = "Email address", example = "john.doe@example.com")
    val email: String,
    @field:Schema(description = "Profile picture URL", nullable = true)
    val profilePictureUrl: String?,
    @field:Schema(description = "Theme setting")
    val theme: ThemeSetting,
    @field:Schema(description = "User timezone", example = "Europe/Berlin")
    val timezone: String,
    @field:Schema(description = "Date format", example = "yyyy-MM-dd")
    val dateFormat: String,
    @field:Schema(description = "Account creation timestamp", example = "1704067200000")
    val createdAt: Long,
    @field:Schema(description = "Email verified status")
    val verified: Boolean
)

data class ProfileRequestDto(
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
    val email: String
)

data class VerificationRequestResponseDto(
    @field:Schema(description = "Next allowed request timestamp", example = "1704067500000")
    val nextCodeAllowedAt: Instant
)
