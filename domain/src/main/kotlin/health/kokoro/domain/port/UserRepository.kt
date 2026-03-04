package health.kokoro.domain.port

import health.kokoro.domain.model.User

interface UserRepository {
    fun findByEmail(mail: String): User?
    fun existsByEmail(email: String): Boolean
    fun save(user: User): User
}