package health.kokoro.domain.port.energy

import health.kokoro.domain.model.energy.EnergyEntry
import java.time.Instant
import java.util.*

interface EnergyEntryRepository {
    fun findAllByUser(uuid: UUID): List<EnergyEntry>
    fun findAllByUserSince(uuid: UUID, since: Instant): List<EnergyEntry>
    fun findAllByUserInRange(uuid: UUID, from: Instant, to: Instant): List<EnergyEntry>
    fun findById(uuid: UUID): EnergyEntry?
    fun save(entry: EnergyEntry)
    fun findLatestByUser(uuid: UUID): EnergyEntry?
    fun findReasonsByUserId(uuid: UUID): List<String>
}