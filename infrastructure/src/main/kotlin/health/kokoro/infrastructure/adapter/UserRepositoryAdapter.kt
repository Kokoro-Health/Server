package health.kokoro.infrastructure.adapter

import health.kokoro.domain.model.User
import health.kokoro.domain.port.UserRepository
import health.kokoro.infrastructure.jpa.user.UserJpaRepository
import health.kokoro.infrastructure.jpa.user.UserMapper
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryAdapter(
    private val jpa: UserJpaRepository, private val mapper: UserMapper
) : UserRepository {
    override fun findByEmail(mail: String): User? {
        return jpa.findByEmailIgnoreCase(mail)?.let { mapper.toDomain(it) }
    }

    override fun existsByEmail(email: String): Boolean {
        return jpa.existsByEmailIgnoreCase(email)
    }

    override fun save(user: User): User {
        return mapper.toDomain(jpa.save(mapper.toEntity(user)))
    }
}