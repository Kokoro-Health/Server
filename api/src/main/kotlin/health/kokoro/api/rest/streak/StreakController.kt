package health.kokoro.api.rest.streak

import health.kokoro.application.usecase.streak.GetCurrentStreak
import health.kokoro.domain.model.user.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/streaks")
@Tag(name = "Streaks", description = "User engagement streaks")
class StreakController(
    private val getCurrentStreak: GetCurrentStreak
) {
    @GetMapping
    @Operation(summary = "Get current streak")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Streak retrieved"),
        ApiResponse(responseCode = "401", description = "Unauthorized")
    )
    fun getCurrentStreak(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<StreakResponseDto> {
        val streakRes = getCurrentStreak.execute(user)

        return ResponseEntity.ok(
            StreakResponseDto(streak = streakRes.currentStreak, streakIncreasedToday = streakRes.streakIncreasedToday)
        )
    }
}
