package health.kokoro.api.rest.streak

import io.swagger.v3.oas.annotations.media.Schema

data class StreakResponseDto(
    @field:Schema(description = "Current streak count in days", example = "7")
    val streak: Int,
    @field:Schema(description = "Whether streak increased today")
    val streakIncreasedToday: Boolean
)
