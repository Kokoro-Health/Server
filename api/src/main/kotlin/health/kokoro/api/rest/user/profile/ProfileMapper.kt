package health.kokoro.api.rest.user.profile

import health.kokoro.application.usecase.user.GetProfile
import org.springframework.stereotype.Component

@Component
class ProfileMapper {
    fun toDto(response: GetProfile.Response) = ProfileResponseDto(
        id = response.id.toString(),
        firstName = response.firstName,
        middleName = response.middleName,
        lastName = response.lastName,
        email = response.email,
        profilePictureUrl = response.profilePictureUrl
            ?: "https://external-content.duckduckgo.com/iu/?u=https%3A%2F%2Fcdn.pixabay.com%2Fphoto%2F2015%2F10%2F05%2F22%2F37%2Fblank-profile-picture-973460_1280.png&f=1&nofb=1&ipt=190a589605909a8addaacc932fbd2363510a5822d14efaec96ed72a363fa9b6a",
        createdAt = response.createdAt.toEpochMilli(),
        theme = response.theme,
        verified = response.verified
    )
}