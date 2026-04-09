package health.kokoro.api.rest.user.settings

import health.kokoro.domain.model.user.settings.LanguageSetting
import health.kokoro.domain.model.user.settings.ThemeSetting
import java.time.Instant


data class SettingsRequestDto(
    val theme: ThemeSetting,
    val language: LanguageSetting,
    val marketingEmails: Boolean,
    val securityAlerts: Boolean,
    val reminderEmails: Boolean,
    val timezone: String,
    val dateFormat: String
)

data class SettingsResponseDto(
    val theme: ThemeSetting,
    val language: LanguageSetting,
    val marketingEmails: Boolean,
    val securityAlerts: Boolean,
    val reminderEmails: Boolean,
    val timezone: String,
    val dateFormat: String,
    val updatedAt: Instant
)
