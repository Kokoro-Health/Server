package health.kokoro.infrastructure.adapter

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
        return mapper.toDomain(jpa.save(mapper.toEntity(settings)))
    }

    override fun existsByUser(user: User): Boolean {
        return jpa.existsByUserId(user.id!!)
    }
}