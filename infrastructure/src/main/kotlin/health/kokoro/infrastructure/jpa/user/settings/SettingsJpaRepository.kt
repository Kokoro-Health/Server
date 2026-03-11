package health.kokoro.infrastructure.jpa.user.settings

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SettingsJpaRepository: JpaRepository<SettingsEntity, UUID> {
    fun findByUserId(uuid: UUID): SettingsEntity?
    fun existsByUserId(uuid: UUID): Boolean
}