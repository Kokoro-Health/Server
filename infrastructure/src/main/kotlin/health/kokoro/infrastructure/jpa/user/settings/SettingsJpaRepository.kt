package health.kokoro.infrastructure.jpa.user.settings

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface SettingsJpaRepository : JpaRepository<SettingsEntity, UUID> {
    fun findByUser_Id(uuid: UUID): SettingsEntity?
    fun existsByUser_Id(uuid: UUID): Boolean
}