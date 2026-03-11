package health.kokoro.domain.port.user

import health.kokoro.domain.model.user.User
import health.kokoro.domain.model.user.settings.Settings

interface SettingsRepository {
    fun findByUser(user: User): Settings?
    fun save(settings: Settings): Settings
    fun existsByUser(user: User): Boolean
}