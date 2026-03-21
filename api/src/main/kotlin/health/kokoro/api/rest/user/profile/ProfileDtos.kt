package health.kokoro.api.rest.user.profile

import health.kokoro.domain.model.user.settings.ThemeSetting
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

data class VerificationRequestResponseDto(
    val nextCodeAllowedAt: Instant
)
