package health.kokoro.domain.port.energy

import health.kokoro.domain.model.energy.EnergyEntry
import java.util.UUID

interface EnergyEntryRepository {
    fun findAllByUser(uuid: UUID): List<EnergyEntry>
    fun save(entry: EnergyEntry)
    fun findLatestByUser(uuid: UUID): EnergyEntry?
}