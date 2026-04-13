package health.kokoro.api.rest.energy

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import java.time.Instant

data class EnergyInfoDateResponseDto(
    @field:Schema(description = "Entry date", example = "1704067200000")
    val date: Instant,
    @field:Schema(description = "Energy level 0-100", example = "75")
    val amount: Int,
    @field:Schema(description = "Reason for energy level", nullable = true, example = "Good sleep")
    val reason: String?
)

data class EnergyInfoResponseDto(
    @field:Schema(description = "Average energy level 0-100", example = "72")
    val energy: Int,
    @field:Schema(description = "Most influential reason", nullable = true)
    val reason: String?,
    @field:Schema(description = "Next allowed entry timestamp", example = "1704153600000")
    val nextEntryAllowed: Instant
)

data class EnergyRequestDto(
    @field:Min(0, message = "Energy must be at least 0")
    @field:Max(100, message = "Energy cannot exceed 100")
    @field:Schema(description = "Energy level 0-100", example = "75")
    val amount: Int,
    @field:Size(max = 220, message = "Reason too long")
    @field:Schema(description = "Reason for energy level", example = "Good sleep, had coffee", nullable = true)
    val reason: String? = null
)

data class EnergyDetailsResponseDto(
    @field:Schema(description = "Most influential negative factor")
    val influentialPositive: ReasonAmountResponseDto,
    @field:Schema(description = "Most influential positive factor")
    val influentialNegative: ReasonAmountResponseDto,
    @field:Schema(description = "Average energy level", example = "72")
    val average: Int,
    @field:Schema(description = "Individual entries for the day")
    val entries: List<EnergyInfoDateResponseDto>
)

data class ReasonAmountResponseDto(
    @field:Schema(description = "Reason name", example = "Sleep quality")
    val reason: String,
    @field:Schema(description = "Impact level 0-100", example = "65")
    val level: Int
)

data class ReasonsResponseDto(
    @field:Schema(description = "Available reasons for energy tracking")
    val reasons: List<String>
)
