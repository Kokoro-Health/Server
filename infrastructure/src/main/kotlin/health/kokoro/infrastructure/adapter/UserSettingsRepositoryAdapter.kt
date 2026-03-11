package health.kokoro.infrastructure.adapter

import health.kokoro.domain.model.user.User
import health.kokoro.domain.model.user.settings.Settings
import health.kokoro.domain.port.user.SettingsRepository
import health.kokoro.infrastructure.jpa.user.settings.NotificationSettingsJpaRepository
import health.kokoro.infrastructure.jpa.user.settings.SettingsJpaRepository
import health.kokoro.infrastructure.jpa.user.settings.SettingsMapper
import org.springframework.stereotype.Repository

@Repository
class UserSettingsRepositoryAdapter(
    private val jpa: SettingsJpaRepository,
    private val notificationJpa: NotificationSettingsJpaRepository,
    private val mapper: SettingsMapper
) : SettingsRepository {
    override fun findByUser(user: User): Settings? {
        return jpa.findByUserId(user.id!!)?.let { mapper.toDomain(it) }
    }

    override fun save(settings: Settings): Settings {
        val notificationEntity = notificationJpa.save(mapper.toEntity(settings.notificationSettings))
        val settingsEntity = mapper.toEntity(settings).apply {
            this.notificationSettings = notificationEntity
        }
        return mapper.toDomain(jpa.save(settingsEntity))
    }

    override fun existsByUser(user: User): Boolean {
        return jpa.existsByUserId(user.id!!)
    }
}