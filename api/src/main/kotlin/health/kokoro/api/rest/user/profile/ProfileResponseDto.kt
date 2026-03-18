package health.kokoro.api.rest.user.profile

import health.kokoro.domain.model.user.settings.ThemeSetting

data class ProfileResponseDto(
    val id: String,
    val firstName: String,
    val middleName: String?,
    val lastName: String,
    val email: String,
    val profilePictureUrl: String,
    val theme: ThemeSetting,
    val createdAt: Long,
    val verified: Boolean
)