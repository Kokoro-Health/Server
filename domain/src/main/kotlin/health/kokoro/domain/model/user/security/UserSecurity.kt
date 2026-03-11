package health.kokoro.domain.model.user.security

import java.util.*

data class UserSecurity(
    val id: UUID? = null,
    val passwordHash: String,
    val mfaEnabled: Boolean,
    val mfaSecret: String?
)