package health.kokoro.domain.model.user.settings

import java.time.Instant
import java.time.ZoneId
import java.util.*

data class Settings(
    val id: UUID? = null,
    var theme: ThemeSetting,
    var language: LanguageSetting,
    var dateFormat: String,
    var timeZone: ZoneId,
    var notificationSettings: NotificationSettings,
    var updatedAt: Instant
)