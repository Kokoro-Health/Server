package health.kokoro.api.rest.energy

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import java.time.Instant
import java.time.LocalDate

data class EnergyInfoDateDto(
    val date: LocalDate,
    val amount: Int
)

data class EnergyInfoDto(
    val energy: Int,
    val nextEntryAllowed: Instant
)

data class EnergyRequestDto(
    @param:Min(0) @param:Max(100) val amount: Int
)
