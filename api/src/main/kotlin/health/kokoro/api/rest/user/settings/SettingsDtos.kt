package health.kokoro.api.rest.user.settings

import health.kokoro.domain.model.user.settings.LanguageSetting
import health.kokoro.domain.model.user.settings.ThemeSetting
import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant

data class SettingsRequestDto(
    @field:Schema(description = "UI theme", example = "DARK")
    val theme: ThemeSetting,
    @field:Schema(description = "Language", example = "EN")
    val language: LanguageSetting,
    @field:Schema(description = "Marketing emails opt-in")
    val marketingEmails: Boolean,
    @field:Schema(description = "Security alert emails")
    val securityAlerts: Boolean,
    @field:Schema(description = "Reminder emails")
    val reminderEmails: Boolean,
    @field:Schema(description = "User timezone", example = "Europe/Berlin")
    val timezone: String,
    @field:Schema(description = "Date format", example = "yyyy-MM-dd")
    val dateFormat: String
)

data class SettingsResponseDto(
    @field:Schema(description = "UI theme")
    val theme: ThemeSetting,
    @field:Schema(description = "Language")
    val language: LanguageSetting,
    @field:Schema(description = "Marketing emails opt-in")
    val marketingEmails: Boolean,
    @field:Schema(description = "Security alert emails")
    val securityAlerts: Boolean,
    @field:Schema(description = "Reminder emails")
    val reminderEmails: Boolean,
    @field:Schema(description = "User timezone", example = "Europe/Berlin")
    val timezone: String,
    @field:Schema(description = "Date format", example = "yyyy-MM-dd")
    val dateFormat: String,
    @field:Schema(description = "Last update timestamp", example = "1704067200000")
    val updatedAt: Instant
)
