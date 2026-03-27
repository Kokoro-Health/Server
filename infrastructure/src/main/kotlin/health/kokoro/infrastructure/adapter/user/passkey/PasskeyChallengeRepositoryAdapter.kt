package health.kokoro.infrastructure.adapter.user.passkey

import health.kokoro.domain.model.user.security.passkey.ChallengeType
import health.kokoro.domain.model.user.security.passkey.PasskeyChallenge
import health.kokoro.domain.port.user.passkey.PasskeyChallengeRepository
import health.kokoro.infrastructure.jpa.user.security.passkey.PasskeyChallengeJpaRepository
import health.kokoro.infrastructure.jpa.user.security.passkey.PasskeyMapper
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*

@Repository
class PasskeyChallengeRepositoryAdapter(
    private val jpa: PasskeyChallengeJpaRepository,
    private val mapper: PasskeyMapper
) : PasskeyChallengeRepository {
    override fun findByUserIdAndType(
        userId: UUID,
        type: ChallengeType
    ): PasskeyChallenge? {
        return jpa.findByUserIdAndType(userId, type)?.let { mapper.toDomain(it) }
    }

    override fun findByEmailAndType(
        email: String,
        type: ChallengeType
    ): PasskeyChallenge? {
        return jpa.findByEmailAndType(email, type)?.let { mapper.toDomain(it) }
    }

    override fun deleteByUserId(userId: UUID) {
        jpa.deleteByUserId(userId)
    }

    override fun deleteByEmail(email: String) {
        jpa.deleteByEmail(email)
    }

    override fun deleteAllByExpiresAtBeforeNow() {
        jpa.deleteAllByExpiresAtBefore(Instant.now())
    }

    override fun save(dto: PasskeyChallenge): PasskeyChallenge {
       return jpa.save(mapper.toEntity(dto)).let { mapper.toDomain(it) }
    }

    override fun delete(dto: PasskeyChallenge) {
       jpa.delete(mapper.toEntity(dto))
    }
}