package health.kokoro.domain.model

import java.time.Instant
import java.util.*

data class User(
    val id: UUID?,
    val firstName: String,
    val middleName: String?,
    val lastName: String,
    val email: String,
    val profilePictureUrl: String?,
    val passwordHash: String,
    val createdAt: Instant,
    val updatedAt: Instant
)
