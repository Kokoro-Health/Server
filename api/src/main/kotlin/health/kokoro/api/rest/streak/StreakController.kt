package health.kokoro.api.rest.streak

import health.kokoro.application.usecase.streak.GetCurrentStreak
import health.kokoro.domain.model.user.User
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/streaks")
class StreakController(
    private val getCurrentStreak: GetCurrentStreak
) {

    @GetMapping
    fun getCurrentStreak(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<StreakResponseDto> {
        val streakRes = getCurrentStreak.execute(user)

        return ResponseEntity.ok(
            StreakResponseDto(streak = streakRes.currentStreak, streakIncreasedToday = streakRes.streakIncreasedToday)
        )
    }
}