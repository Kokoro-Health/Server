package health.kokoro.api.rest.energy

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

data class EnergyRequestDto(
    @param:Min(0) @param:Max(100) val amount: Int
)