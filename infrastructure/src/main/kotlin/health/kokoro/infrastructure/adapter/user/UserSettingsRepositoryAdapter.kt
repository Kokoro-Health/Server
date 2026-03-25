package health.kokoro.infrastructure.adapter.user

import health.kokoro.domain.model.user.User
import health.kokoro.domain.model.user.settings.Settings
import health.kokoro.domain.port.user.SettingsRepository
import health.kokoro.infrastructure.jpa.user.settings.SettingsJpaRepository
import health.kokoro.infrastructure.jpa.user.settings.SettingsMapper
import org.springframework.stereotype.Repository

@Repository
class UserSettingsRepositoryAdapter(
    private val jpa: SettingsJpaRepository,
    private val mapper: SettingsMapper
) : SettingsRepository {
    override fun findByUser(user: User): Settings? {
        return jpa.findByUserId(user.id!!)?.let { mapper.toDomain(it) }
    }

    override fun save(settings: Settings): Settings {
        val notificationEntity = mapper.toEntity(settings.notificationSettings)
        val settingsEntity = mapper.toEntity(settings).apply {
            this.notificationSettings = notificationEntity
        }
        return mapper.toDomain(jpa.save(settingsEntity))
    }

    override fun existsByUser(user: User): Boolean {
        return jpa.existsByUserId(user.id!!)
    }

    override fun update(user: User, settings: Settings) {
        val existingSettings = jpa.findByUserId(user.id!!)
            ?: throw IllegalStateException("Settings not found for user")

        existingSettings.theme = settings.theme
        existingSettings.language = settings.language
        existingSettings.updatedAt = settings.updatedAt

        existingSettings.notificationSettings.apply {
            marketingEmails = settings.notificationSettings.marketingEmails
            securityAlerts = settings.notificationSettings.securityAlerts
            reminderEmails = settings.notificationSettings.reminderEmails
        }

        jpa.save(existingSettings)
    }
}