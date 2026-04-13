package health.kokoro.api.rest.user.profile

import health.kokoro.application.usecase.user.GetProfile
import health.kokoro.application.usecase.user.UpdateProfile
import org.springframework.stereotype.Component

@Component
class ProfileMapper {
    fun toDto(response: GetProfile.Response) = ProfileResponseDto(
        id = response.id.toString(),
        firstName = response.firstName,
        middleName = response.middleName,
        lastName = response.lastName,
        email = response.email,
        profilePictureUrl = response.profilePictureUrl,
        createdAt = response.createdAt.toEpochMilli(),
        theme = response.theme,
        timezone = response.timezone,
        dateFormat = response.dateFormat,
        verified = response.verified
    )

    fun toCommand(request: ProfileRequestDto): UpdateProfile.Command {
        return UpdateProfile.Command(
            firstName = request.firstName,
            middleName = request.middleName,
            lastName = request.lastName,
            email = request.email
        )
    }
}
