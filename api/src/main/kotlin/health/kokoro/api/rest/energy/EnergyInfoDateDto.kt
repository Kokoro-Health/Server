package health.kokoro.api.rest.energy

import java.time.LocalDate

data class EnergyInfoDateDto(
    val date: LocalDate,
    val amount: Int
)