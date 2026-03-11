package health.kokoro.infrastructure.jpa.user.settings

import health.kokoro.domain.model.user.settings.NotificationSettings
import health.kokoro.domain.model.user.settings.Settings
import health.kokoro.infrastructure.jpa.user.UserJpaRepository
import org.springframework.stereotype.Component

@Component
class SettingsMapper(
    private val userJpaRepository: UserJpaRepository,
    private val settingsJpaRepository: SettingsJpaRepository,
) {
    fun toDomain(entity: SettingsEntity): Settings {
        return Settings(
            userId = entity.user.id!!,
            theme = entity.theme,
            language = entity.language,
            notificationSettings = toDomain(entity.notificationSettings),
            updatedAt = entity.updatedAt
        )
    }

    fun toEntity(domain: Settings): SettingsEntity {
        val user = userJpaRepository.findById(domain.userId).get()
        val existingSettings = settingsJpaRepository.findByUserId(domain.userId)
        
        return existingSettings?.apply {
            theme = domain.theme
            language = domain.language
            updatedAt = domain.updatedAt
        }
            ?: SettingsEntity(
                user = user,
                theme = domain.theme,
                language = domain.language,
                notificationSettings = toEntity(domain.notificationSettings)
            )
    }

    fun toEntity(domain: NotificationSettings): NotificationSettingsEntity {
        return NotificationSettingsEntity(
            marketingEmails = domain.marketingEmails,
            securityAlerts = domain.securityAlerts,
            reminderEmails = domain.reminderEmails
        )
    }

    private fun toDomain(entity: NotificationSettingsEntity): NotificationSettings {
        return NotificationSettings(
            marketingEmails = entity.marketingEmails,
            securityAlerts = entity.securityAlerts,
            reminderEmails = entity.reminderEmails
        )
    }
}