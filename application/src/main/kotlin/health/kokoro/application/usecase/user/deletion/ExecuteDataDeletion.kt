package health.kokoro.application.usecase.user.deletion

import health.kokoro.domain.port.data.DataDeletionRequestRepository
import health.kokoro.domain.port.user.UserRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class ExecuteDataDeletion(
    private val userRepository: UserRepository,
    private val deletionRequestRepository: DataDeletionRequestRepository
) {
    fun execute() {
        val now = Instant.now()
        val deletableDate = now.minus(7, ChronoUnit.DAYS)

        val requests = deletionRequestRepository.findDeletableRequests(deletableDate)

        for (request in requests) {
            userRepository.deleteById(request.userId)
            deletionRequestRepository.deleteByUserId(request.userId)
        }
    }
}