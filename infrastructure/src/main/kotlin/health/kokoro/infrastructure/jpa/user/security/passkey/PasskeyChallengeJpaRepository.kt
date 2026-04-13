package health.kokoro.infrastructure.jpa.user.security.passkey

import health.kokoro.domain.model.user.security.passkey.ChallengeType
import health.kokoro.infrastructure.jpa.user.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*

@Repository
interface PasskeyChallengeJpaRepository : JpaRepository<PasskeyChallengeEntity, UUID> {
    fun findByUserIdAndType(userId: UUID, type: ChallengeType): PasskeyChallengeEntity?
    fun findByEmailAndType(email: String, type: ChallengeType): PasskeyChallengeEntity?
    fun deleteByUserId(userId: UUID)
    fun deleteByEmail(email: String)
    fun deleteAllByExpiresAtBefore(now: Instant)
    fun user(user: UserEntity): MutableList<PasskeyChallengeEntity>

    fun deleteAllByUserId(userId: UUID)
}