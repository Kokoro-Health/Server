package health.kokoro.api.rest.streak

data class StreakResponseDto(
    val streak: Int,
    val streakIncreasedToday: Boolean
)