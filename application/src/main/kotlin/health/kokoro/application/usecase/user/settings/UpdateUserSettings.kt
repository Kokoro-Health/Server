package health.kokoro.application.usecase.user.settings

import health.kokoro.domain.model.user.settings.Settings
import health.kokoro.domain.port.user.SettingsRepository
import org.springframework.stereotype.Service

@Service
class UpdateUserSettings(
    private val repo: SettingsRepository
) {
    fun execute(settings: Settings): Settings {
        return repo.save(settings)
    }
}