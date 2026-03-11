package health.kokoro.api.rest.user.settings

import health.kokoro.domain.model.user.User
import health.kokoro.domain.model.user.settings.NotificationSettings
import health.kokoro.domain.model.user.settings.Settings
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.Instant

@Component
class SettingsDtoMapper(
    private val clock: Clock
) {
    fun toDto(settings: Settings): SettingsResponseDto {
        return SettingsResponseDto(
            theme = settings.theme,
            language = settings.language,
            marketingEmails = settings.notificationSettings.marketingEmails,
            reminderEmails = settings.notificationSettings.reminderEmails,
            securityAlerts = settings.notificationSettings.securityAlerts,
            updatedAt = settings.updatedAt
        )
    }

    fun toDomain(dto: SettingsRequestDto, user: User): Settings {
        return Settings(
            userId = user.id!!,
            theme = dto.theme,
            language = dto.language,
            notificationSettings = NotificationSettings(
                marketingEmails = dto.marketingEmails,
                securityAlerts = dto.securityAlerts,
                reminderEmails = dto.reminderEmails
            ),
            updatedAt = Instant.now(clock)
        )
    }
}