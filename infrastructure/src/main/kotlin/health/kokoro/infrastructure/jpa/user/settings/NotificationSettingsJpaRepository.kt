package health.kokoro.infrastructure.jpa.user.settings

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface NotificationSettingsJpaRepository : JpaRepository<NotificationSettingsEntity, UUID> {
}