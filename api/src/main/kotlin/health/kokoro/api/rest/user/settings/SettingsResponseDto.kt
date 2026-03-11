package health.kokoro.api.rest.user.settings

import health.kokoro.domain.model.user.settings.LanguageSetting
import health.kokoro.domain.model.user.settings.ThemeSetting
import java.time.Instant

class SettingsResponseDto(
    private val theme: ThemeSetting,
    private val language: LanguageSetting,
    private val marketingEmails: Boolean,
    private val securityAlerts: Boolean,
    private val reminderEmails: Boolean,
    private val updatedAt: Instant
)