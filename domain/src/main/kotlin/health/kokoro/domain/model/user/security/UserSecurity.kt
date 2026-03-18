package health.kokoro.domain.model.user.security

import java.time.Instant
import java.util.*

data class UserSecurity(
    val id: UUID? = null,
    var passwordHash: String,
    var mfaEnabled: Boolean,
    var mfaSecret: String?,
    var verified: Boolean,
    var verificationCode: String?,
    var verificationCodeRequestedAt: Instant?,
    var passwordResetCode: String? = null,
    var passwordResetCodeRequestedAt: Instant? = null
)