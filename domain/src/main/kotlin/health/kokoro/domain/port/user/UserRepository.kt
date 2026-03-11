package health.kokoro.domain.port.user

import health.kokoro.domain.model.user.User
import java.util.UUID

interface UserRepository {
    fun findByEmail(mail: String): User?
    fun findById(id: UUID): User?
    fun existsByEmail(email: String): Boolean
    fun save(user: User): User
}