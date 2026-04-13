package health.kokoro.infrastructure.jpa.user.security.passkey

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface PasskeyJpaRepository : JpaRepository<PasskeyEntity, UUID> {
    fun findByUserId(userId: UUID): List<PasskeyEntity>
    fun findByCredentialId(credentialId: String): PasskeyEntity?

    fun deleteAllByUserId(userId: UUID)
}