package health.kokoro.infrastructure.jpa.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface UserJpaRepository: JpaRepository<UserEntity, UUID> {
}