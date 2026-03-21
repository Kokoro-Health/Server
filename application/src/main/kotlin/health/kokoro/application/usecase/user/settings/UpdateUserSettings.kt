package health.kokoro.application.usecase.user.settings

import health.kokoro.domain.model.user.User
import health.kokoro.domain.model.user.settings.Settings
import health.kokoro.domain.port.user.SettingsRepository
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Instant

@Service
class UpdateUserSettings(
    private val repo: SettingsRepository,
) {
    fun execute(user: User, settings: Settings) {
        val updatedSettings = settings.copy(updatedAt = Instant.now())
        repo.update(user, updatedSettings)
    }
}