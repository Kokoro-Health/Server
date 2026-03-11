package health.kokoro.api.rest.user.settings

import health.kokoro.domain.model.user.settings.LanguageSetting
import health.kokoro.domain.model.user.settings.ThemeSetting

data class SettingsRequestDto(
    val theme: ThemeSetting,
    val language: LanguageSetting,
    val marketingEmails: Boolean,
    val securityAlerts: Boolean,
    val reminderEmails: Boolean,
)