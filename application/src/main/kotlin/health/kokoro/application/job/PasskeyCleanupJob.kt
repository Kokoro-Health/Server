package health.kokoro.application.job

import health.kokoro.domain.port.user.passkey.PasskeyChallengeRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class PasskeyCleanupJob(     private val repository: PasskeyChallengeRepository
) {
    @Scheduled(fixedDelay = 60_000)
    fun purgeExpired() {
        repository.deleteAllByExpiresAtBeforeNow()
    }
}