package health.kokoro.infrastructure.adapter.user

import health.kokoro.domain.model.user.User
import health.kokoro.domain.model.user.security.UserSecurity
import health.kokoro.domain.port.user.UserSecurityRepository
import health.kokoro.infrastructure.jpa.user.security.UserSecurityJpaRepository
import health.kokoro.infrastructure.jpa.user.security.UserSecurityMapper
import org.springframework.stereotype.Repository

@Repository
class UserSecurityRepositoryAdapter(
    private val jpa: UserSecurityJpaRepository,
    private val mapper: UserSecurityMapper
) : UserSecurityRepository {
    override fun save(security: UserSecurity): UserSecurity {
        return jpa.save(mapper.toEntity(security)).let { mapper.toDomain(it) }
    }

    override fun update(user: User, secret: String) {
        val securityEntity = jpa.findById(user.security.id!!)
            .orElseThrow { IllegalStateException("Security not found for user") }
        securityEntity.mfaSecret = secret
        jpa.save(securityEntity)
    }

    override fun enableMfa(user: User) {
        val securityEntity = jpa.findById(user.security.id!!)
            .orElseThrow { IllegalStateException("Security not found for user") }
        securityEntity.mfaEnabled = true
        jpa.save(securityEntity)
    }

    override fun disableMfa(user: User) {
        val securityEntity = jpa.findById(user.security.id!!)
            .orElseThrow { IllegalStateException("Security not found for user") }
        securityEntity.mfaEnabled = false
        securityEntity.mfaSecret = null
        jpa.save(securityEntity)
    }

    override fun findByPasswordResetCode(code: String): UserSecurity? {
        return jpa.findByPasswordResetCode(code)?.let { mapper.toDomain(it) }
    }
}