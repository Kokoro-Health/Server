package health.kokoro.domain.port.user.passkey

import health.kokoro.domain.model.user.security.passkey.ChallengeType
import health.kokoro.domain.model.user.security.passkey.PasskeyChallenge
import java.util.*

interface PasskeyChallengeRepository {
    fun findByUserIdAndType(userId: UUID, type: ChallengeType): PasskeyChallenge?
    fun findByEmailAndType(email: String, type: ChallengeType): PasskeyChallenge?
    fun deleteByUserId(userId: UUID)
    fun deleteAllByUserId(userId: UUID)
    fun deleteByEmail(email: String)
    fun deleteAllByExpiresAtBeforeNow()
    fun save(dto: PasskeyChallenge): PasskeyChallenge
    fun delete(dto: PasskeyChallenge)
}