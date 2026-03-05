package health.kokoro.domain.port

import health.kokoro.domain.model.EnergyEntry
import java.util.UUID

interface EnergyEntryRepository {
    fun findAllByUser(uuid: UUID): List<EnergyEntry>
}