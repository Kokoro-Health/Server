package health.kokoro.domain.port.user

import health.kokoro.domain.model.user.User
import health.kokoro.domain.model.user.security.UserSecurity

interface UserSecurityRepository {
    fun save(security: UserSecurity): UserSecurity
    fun update(user: User, secret: String)
    fun enableMfa(user: User)
    fun disableMfa(user: User)
}