package health.kokoro.infrastructure.jpa.energy

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface EnergyEntryJpaRepository : JpaRepository<EnergyEntryEntity, UUID> {
    fun findAllByUserId(uuid: UUID): List<EnergyEntryEntity>
}