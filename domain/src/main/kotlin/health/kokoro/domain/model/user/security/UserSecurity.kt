package health.kokoro.domain.model.user.security

import java.time.Instant
import java.util.*

data class UserSecurity(
    val id: UUID? = null,
    val passwordHash: String,
    val mfaEnabled: Boolean,
    val mfaSecret: String?,
    val verified: Boolean,
    val verificationCode: String?,
    val verificationCodeRequestedAt: Instant?
)