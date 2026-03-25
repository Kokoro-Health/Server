package health.kokoro.api.rest.user.settings

import health.kokoro.domain.model.user.settings.NotificationSettings
import health.kokoro.domain.model.user.settings.Settings
import health.kokoro.infrastructure.converter.ZoneIdConverter
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class SettingsDtoMapper {
    fun toDto(settings: Settings): SettingsResponseDto {
        return SettingsResponseDto(
            theme = settings.theme,
            language = settings.language,
            marketingEmails = settings.notificationSettings.marketingEmails,
            reminderEmails = settings.notificationSettings.reminderEmails,
            securityAlerts = settings.notificationSettings.securityAlerts,
            timezone = settings.timeZone.id,
            dateFormat = settings.dateFormat,
            updatedAt = settings.updatedAt
        )
    }

    fun toDomain(dto: SettingsRequestDto): Settings {
        val zoneId = ZoneIdConverter().convertToEntityAttribute(dto.timezone)
            ?: throw IllegalArgumentException("Invalid timezone")
        return Settings(
            theme = dto.theme,
            language = dto.language,
            dateFormat = dto.dateFormat,
            timeZone = zoneId,
            notificationSettings = NotificationSettings(
                marketingEmails = dto.marketingEmails,
                securityAlerts = dto.securityAlerts,
                reminderEmails = dto.reminderEmails
            ),
            updatedAt = Instant.now()
        )
    }
}