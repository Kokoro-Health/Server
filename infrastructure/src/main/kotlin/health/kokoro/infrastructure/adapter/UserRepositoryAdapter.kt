package health.kokoro.infrastructure.adapter

import health.kokoro.domain.model.user.User
import health.kokoro.domain.port.user.UserRepository
import health.kokoro.infrastructure.jpa.user.UserJpaRepository
import health.kokoro.infrastructure.jpa.user.UserMapper
import org.springframework.stereotype.Repository
import java.util.*
import kotlin.jvm.optionals.getOrNull

@Repository
class UserRepositoryAdapter(
    private val jpa: UserJpaRepository, private val mapper: UserMapper
) : UserRepository {
    override fun findByEmail(mail: String): User? {
        return jpa.findByEmailIgnoreCase(mail)?.let { mapper.toDomain(it) }
    }

    override fun findById(id: UUID): User? {
        return jpa.findById(id).getOrNull()?.let { mapper.toDomain(it) }
    }

    override fun existsByEmail(email: String): Boolean {
        return jpa.existsByEmailIgnoreCase(email)
    }

    override fun save(user: User): User {
        return mapper.toDomain(jpa.save(mapper.toEntity(user)))
    }
}