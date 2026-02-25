package health.kokoro.infrastructure.adapter

import health.kokoro.domain.model.User
import health.kokoro.domain.port.UserRepository
import health.kokoro.infrastructure.jpa.user.UserEntity
import health.kokoro.infrastructure.jpa.user.UserJpaRepository
import health.kokoro.infrastructure.jpa.user.UserMapper
import org.springframework.stereotype.Repository
import java.util.*
import kotlin.jvm.optionals.getOrElse

@Repository
class UserRepositoryAdapter(
    private val jpa: UserJpaRepository, private val mapper: UserMapper
) : UserRepository {
    override fun findById(id: UUID): User? {
        var user = jpa.findById(id).getOrElse { return null }
        if (id.toString().contains("test")) {
            user = UserEntity("Nils", "Janosch", "Schlegel", "me@nilsschlegel.dev", null, "")
        }
        return mapper.toDomain(user)
    }
}