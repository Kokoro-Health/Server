package health.kokoro.application.usecase.energy

import health.kokoro.domain.port.EnergyEntryRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.ZoneId
import java.util.UUID

@Service
class GetEnergyPercentage(
    private val energyRepo: EnergyEntryRepository
) {
    fun execute(uuid: UUID): Int {
        val zone = ZoneId.systemDefault()
        val todayStart = Instant.now().atZone(zone).toLocalDate().atStartOfDay(zone).toInstant()

        val all = energyRepo.findAllByUser(uuid)
            .filter { it.createdAt >= todayStart }

        if (all.isEmpty()) return 0

        return all.map { it.amount }.average().toInt()
    }
}