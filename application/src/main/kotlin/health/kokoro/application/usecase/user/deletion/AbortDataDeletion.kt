package health.kokoro.application.usecase.user.deletion

import health.kokoro.domain.port.data.DataDeletionRequestRepository
import org.springframework.stereotype.Service

@Service
class AbortDataDeletion(
    private val deletionRequestRepository: DataDeletionRequestRepository
) {
    fun execute(userId: java.util.UUID) {
        deletionRequestRepository.abort(userId)
    }
}