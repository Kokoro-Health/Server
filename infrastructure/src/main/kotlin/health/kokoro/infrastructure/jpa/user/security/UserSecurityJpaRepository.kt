package health.kokoro.infrastructure.jpa.user.security

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserSecurityJpaRepository : JpaRepository<UserSecurityEntity, UUID>