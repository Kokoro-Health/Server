package health.kokoro.api.rest.user.profile

import health.kokoro.domain.model.user.settings.ThemeSetting
import jakarta.validation.constraints.Email
import java.time.Instant

data class ProfileResponseDto(
    val id: String,
    val firstName: String,
    val middleName: String?,
    val lastName: String,
    val email: String,
    val profilePictureUrl: String,
    val theme: ThemeSetting,
    val timezone: String,
    val dateFormat: String,
    val createdAt: Long,
    val verified: Boolean
)

data class ProfileRequestDto(
    val firstName: String,
    val middleName: String?,
    val lastName: String,
    @field:Email val email: String
)

data class VerificationRequestResponseDto(
    val nextCodeAllowedAt: Instant
)

data class DataDeletionConfirmRequestDto(
    val code: String
)
