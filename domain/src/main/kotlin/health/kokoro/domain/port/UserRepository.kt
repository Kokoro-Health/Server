package health.kokoro.domain.port

import health.kokoro.domain.model.User
import java.util.UUID

interface UserRepository {
    fun findById(id: UUID): User?
}