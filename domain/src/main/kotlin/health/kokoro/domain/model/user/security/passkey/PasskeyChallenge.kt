package health.kokoro.domain.model.user.security.passkey

import java.time.Instant
import java.util.*

data class PasskeyChallenge(
    val id: UUID,
    val userId: UUID?,
    val email: String?,
    val data: String,
    val type: ChallengeType,
    val expiresAt: Instant,
    val createdAt: Instant
)
