package health.kokoro.application.usecase.auth.passkey

import health.kokoro.domain.error.PasskeyNotFoundException
import health.kokoro.domain.error.PasskeyOwnershipException
import health.kokoro.domain.port.user.passkey.PasskeyRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class DeletePasskey(
    private val passkeyRepository: PasskeyRepository,
) {
    fun execute(passkeyId: UUID, requestingUserId: UUID) {
        val passkey = passkeyRepository.findById(passkeyId)
            ?: throw PasskeyNotFoundException(passkeyId)

        if (passkey.userId != requestingUserId) {
            throw PasskeyOwnershipException()
        }

        passkeyRepository.delete(passkey)
    }
}
