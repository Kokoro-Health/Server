package health.kokoro.domain.model.user

import health.kokoro.domain.model.file.FileUpload
import health.kokoro.domain.model.user.security.UserSecurity
import health.kokoro.domain.model.user.settings.Settings
import java.time.Instant
import java.util.*

data class User(
    val id: UUID?,
    val firstName: String,
    val middleName: String?,
    val lastName: String,
    val email: String,
    var profilePicture: FileUpload?,
    val security: UserSecurity,
    val settings: Settings,
    val createdAt: Instant,
    val updatedAt: Instant
)