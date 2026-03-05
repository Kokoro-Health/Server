package health.kokoro.api.rest.energy

import java.time.Instant

data class EnergyInfoDto(
    val energy: Int,
    val nextEntryAllowed: Instant
)