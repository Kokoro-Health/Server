package health.kokoro.application.job

import health.kokoro.application.usecase.user.deletion.ExecuteDataDeletion
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class DataDeletionCleanupJob(
    private val executeDataDeletion: ExecuteDataDeletion
) {
    @Scheduled(fixedDelay = 60_000)
    fun execute() {
        executeDataDeletion.execute()
    }
}