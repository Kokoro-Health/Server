package health.kokoro.application.usecase.energy

import health.kokoro.domain.port.EnergyEntryRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class GetEnergyPercentage(
    private val energyRepo: EnergyEntryRepository
) {
    fun execute(uuid: UUID): Int {
        var energy = 0
        energyRepo.findAllByUser(uuid).forEach { energy += it.amount }
        return energy
    }

}