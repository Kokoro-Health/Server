package health.kokoro.application.usecase.user.deletion

import health.kokoro.domain.error.InvalidVerificationCodeException
import health.kokoro.domain.port.data.DataDeletionRequestRepository
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ConfirmDataDeletion(
    private val deletionRequestRepository: DataDeletionRequestRepository
) {
    fun execute(userId: UUID, code: String) {
        val request = deletionRequestRepository.findByUserId(userId)
            ?: throw InvalidVerificationCodeException()

        if (request.confirmationCode != code) {
            throw InvalidVerificationCodeException()
        }

        if (request.confirmed) {
            return
        }

        deletionRequestRepository.confirm(userId, code)
    }
}