package health.kokoro.application.usecase.user.settings

import health.kokoro.domain.model.user.User
import health.kokoro.domain.model.user.settings.LanguageSetting
import health.kokoro.domain.model.user.settings.NotificationSettings
import health.kokoro.domain.model.user.settings.Settings
import health.kokoro.domain.model.user.settings.ThemeSetting
import health.kokoro.domain.port.user.SettingsRepository
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Instant

@Service
class GetUserSettings(
    private val repo: SettingsRepository,
    private val clock: Clock
) {
    fun execute(user: User): Settings {
        if (!repo.existsByUser(user)) {
            val newSettings = Settings(
                userId = user.id!!,
                theme = ThemeSetting.LIGHT,
                language = LanguageSetting.ENGLISH,
                notificationSettings = NotificationSettings(
                    marketingEmails = true,
                    securityAlerts = true,
                    reminderEmails = true
                ),
                updatedAt = Instant.now(clock)
            )
            return repo.save(newSettings)
        }
        return repo.findByUser(user) ?: throw IllegalStateException("User settings not found")
    }
}