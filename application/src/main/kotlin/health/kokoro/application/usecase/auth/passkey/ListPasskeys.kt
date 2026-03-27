package health.kokoro.application.usecase.auth.passkey

import health.kokoro.domain.model.user.security.passkey.Passkey
import health.kokoro.domain.port.user.passkey.PasskeyRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class ListPasskeys(
    private val passkeyRepository: PasskeyRepository,
) {
    fun execute(userId: UUID): List<Passkey> {
        return passkeyRepository.findByUserId(userId)
    }
}
