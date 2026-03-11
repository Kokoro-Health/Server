package health.kokoro.application.usecase.user

import health.kokoro.domain.port.user.UserRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class GetProfile(
    private val userRepository: UserRepository
) {
    fun execute(id: UUID): Response {
        val user = userRepository.findById(id) ?: throw IllegalArgumentException("User with id $id not found")
        return Response(
            id = user.id!!,
            firstName = user.firstName,
            middleName = user.middleName,
            lastName = user.lastName,
            email = user.email,
            profilePictureUrl = user.profilePictureUrl,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        )
    }

    data class Response(
        val id: UUID,
        val firstName: String,
        val middleName: String?,
        val lastName: String,
        val email: String,
        val profilePictureUrl: String?,
        val createdAt: Instant,
        val updatedAt: Instant
    )
}