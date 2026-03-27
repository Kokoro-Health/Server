package health.kokoro.infrastructure.adapter.user.passkey

import health.kokoro.domain.model.user.security.passkey.Passkey
import health.kokoro.domain.port.user.passkey.PasskeyRepository
import health.kokoro.infrastructure.jpa.user.UserJpaRepository
import health.kokoro.infrastructure.jpa.user.security.passkey.PasskeyJpaRepository
import health.kokoro.infrastructure.jpa.user.security.passkey.PasskeyMapper
import org.springframework.stereotype.Repository
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Repository
class PasskeyRepositoryAdapter(
    private val jpa: PasskeyJpaRepository,
    private val mapper: PasskeyMapper,
    private val userJpa: UserJpaRepository
) : PasskeyRepository {
    override fun findByUserId(userId: UUID): List<Passkey> {
        return jpa.findByUserId(userId).map { mapper.toDomain(it) }
    }

    override fun findByCredentialId(credentialId: String): Passkey? {
        return jpa.findByCredentialId(credentialId)?.let { mapper.toDomain(it) }
    }

    override fun findByEmail(email: String): List<Passkey> {
     val user = userJpa.findByEmailIgnoreCase(email) ?: throw IllegalArgumentException("Could not find user $email")
        return jpa.findByUserId(user.id!!).map { mapper.toDomain(it) }
    }

    override fun save(dto: Passkey): Passkey {
       return jpa.save(mapper.toEntity(dto)).let { mapper.toDomain(it) }
    }

    override fun findById(id: UUID): Passkey? {
        return jpa.findById(id).getOrNull()?.let { mapper.toDomain(it) }
    }

    override fun delete(dto: Passkey) {
       jpa.delete(mapper.toEntity(dto))
    }
}