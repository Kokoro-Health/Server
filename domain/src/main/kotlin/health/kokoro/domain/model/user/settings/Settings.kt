package health.kokoro.domain.model.user.settings

import java.time.Instant
import java.util.UUID

data class Settings(
    var userId: UUID,
    var theme: ThemeSetting,
    var language: LanguageSetting,
    var notificationSettings: NotificationSettings,
    var updatedAt: Instant
)