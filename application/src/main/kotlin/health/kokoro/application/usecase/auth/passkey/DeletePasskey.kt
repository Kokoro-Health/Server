package health.kokoro.application.usecase.auth.passkey

import health.kokoro.domain.port.user.passkey.PasskeyRepository
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class DeletePasskey(
    private val passkeyRepository: PasskeyRepository,
) {
    fun execute(passkeyId: UUID, requestingUserId: UUID) {
        val passkey = passkeyRepository.findById(passkeyId)
            ?: throw IllegalStateException("Passkey not found")

        if (passkey.userId != requestingUserId) {
            throw AccessDeniedException("You do not own this passkey")
        }

        passkeyRepository.delete(passkey)
    }
}
