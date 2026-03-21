package health.kokoro.infrastructure.jpa.user.settings

import health.kokoro.domain.model.user.settings.NotificationSettings
import health.kokoro.domain.model.user.settings.Settings
import org.springframework.stereotype.Component

@Component
class SettingsMapper {
    fun toDomain(entity: SettingsEntity): Settings {
        return Settings(
            id = entity.id,
            theme = entity.theme,
            language = entity.language,
            timeZone = entity.timezone,
            dateFormat = entity.dateFormat,
            notificationSettings = toDomain(entity.notificationSettings),
            updatedAt = entity.updatedAt
        )
    }

    fun toEntity(domain: Settings): SettingsEntity {
        val entity = SettingsEntity(
            theme = domain.theme,
            language = domain.language,
            notificationSettings = toEntity(domain.notificationSettings),
            timezone = domain.timeZone,
            dateFormat = domain.dateFormat
        )
        entity.id = domain.id
        entity.updatedAt = domain.updatedAt
        return entity
    }

    fun toEntity(domain: NotificationSettings): NotificationSettingsEntity {
        val entity = NotificationSettingsEntity(
            marketingEmails = domain.marketingEmails,
            securityAlerts = domain.securityAlerts,
            reminderEmails = domain.reminderEmails
        )
        entity.id = domain.id
        return entity
    }

    private fun toDomain(entity: NotificationSettingsEntity): NotificationSettings {
        return NotificationSettings(
            id = entity.id,
            marketingEmails = entity.marketingEmails,
            securityAlerts = entity.securityAlerts,
            reminderEmails = entity.reminderEmails
        )
    }
}