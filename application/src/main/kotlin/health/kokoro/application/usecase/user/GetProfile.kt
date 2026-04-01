package health.kokoro.application.usecase.user

import health.kokoro.application.usecase.file.GetFileUrl
import health.kokoro.domain.model.user.settings.ThemeSetting
import health.kokoro.domain.port.user.UserRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class GetProfile(
    private val userRepository: UserRepository,
    private val getFileUrl: GetFileUrl
) {
    fun execute(id: UUID): Response {
        val user = userRepository.findById(id) ?: throw IllegalArgumentException("User with id $id not found")
        return Response(
            id = user.id!!,
            firstName = user.firstName,
            middleName = user.middleName,
            lastName = user.lastName,
            email = user.email,
            profilePictureUrl = user.profilePicture?.let { getFileUrl.execute(it) },
            createdAt = user.createdAt,
            updatedAt = user.updatedAt,
            theme = user.settings.theme,
            verified = user.security.verified,
            timezone = user.settings.timeZone.id,
            dateFormat = user.settings.dateFormat,
        )
    }

    data class Response(
        val id: UUID,
        val firstName: String,
        val middleName: String?,
        val lastName: String,
        val email: String,
        val profilePictureUrl: String?,
        val theme: ThemeSetting,
        val verified: Boolean,
        val timezone: String,
        val dateFormat: String,
        val createdAt: Instant,
        val updatedAt: Instant
    )
}