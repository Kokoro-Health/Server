package health.kokoro.api.rest.energy

import jakarta.annotation.Nullable
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import java.time.Instant

data class EnergyInfoDateDto(
    val date: Instant,
    val amount: Int,
    val reason: String?
)

data class EnergyInfoDto(
    val energy: Int,
    val reason: String?,
    val nextEntryAllowed: Instant
)

data class EnergyRequestDto(
    @field:Min(0) @field:Max(100) val amount: Int, @field:Size(max = 220) @field:Nullable val reason: String? = null
)

data class EnergyDetailsDto(
    val influentialPositive: ReasonAmount,
    val influentialNegative: ReasonAmount,
    val average: Int,
    val entries: List<EnergyInfoDateDto>

)

data class ReasonAmount(
    val reason: String,
    val level: Int
)

data class ReasonsResponseDto(val reasons: List<String>)