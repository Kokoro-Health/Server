package health.kokoro.infrastructure.jpa.energy

import health.kokoro.domain.model.energy.EnergyEntry
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface EnergyEntryJpaRepository : JpaRepository<EnergyEntryEntity, UUID> {
    fun findAllByUserId(uuid: UUID): List<EnergyEntryEntity>
}