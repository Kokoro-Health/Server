package health.kokoro.api.rest.energy

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import java.time.Instant

data class EnergyInfoDateResponseDto(
    val date: Instant,
    val amount: Int,
    @field:Schema(nullable = true) val reason: String?
)

data class EnergyInfoResponseDto(
    val energy: Int,
    val reason: String?,
    val nextEntryAllowed: Instant
)

data class EnergyRequestDto(
    @field:Min(0) @field:Max(100) val amount: Int,
    @field:Size(max = 220) @field:Schema(nullable = true) val reason: String? = null
)

data class EnergyDetailsResponseDto(
    val influentialPositive: ReasonAmountResponseDto,
    val influentialNegative: ReasonAmountResponseDto,
    val average: Int,
    val entries: List<EnergyInfoDateResponseDto>

)

data class ReasonAmountResponseDto(
    val reason: String,
    val level: Int
)

data class ReasonsResponseDto(val reasons: List<String>)