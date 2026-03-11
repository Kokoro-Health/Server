package health.kokoro.api.rest.user.profile

data class ProfileResponseDto(
    val id: String,
    val firstName: String,
    val middleName: String?,
    val lastName: String,
    val email: String,
    val profilePictureUrl: String,
    val createdAt: Long
)