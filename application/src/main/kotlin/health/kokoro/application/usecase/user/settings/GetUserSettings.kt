package health.kokoro.application.usecase.user.settings

import health.kokoro.domain.model.user.User
import health.kokoro.domain.model.user.settings.LanguageSetting
import health.kokoro.domain.model.user.settings.NotificationSettings
import health.kokoro.domain.model.user.settings.Settings
import health.kokoro.domain.model.user.settings.ThemeSetting
import health.kokoro.domain.port.user.SettingsRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.ZoneId

@Service
class GetUserSettings(
    private val repo: SettingsRepository,
) {
    fun execute(user: User): Settings {
        return repo.findByUser(user) ?: run {
            val newSettings = Settings(
                theme = ThemeSetting.LIGHT,
                language = LanguageSetting.ENGLISH,
                dateFormat = "dd-MM-yyyy",
                timeZone = ZoneId.systemDefault(),
                notificationSettings = NotificationSettings(
                    marketingEmails = true,
                    securityAlerts = true,
                    reminderEmails = true
                ),
                updatedAt = Instant.now()
            )
            repo.save(newSettings)
        }
    }
}