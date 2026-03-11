package health.kokoro.application.usecase.auth

import health.kokoro.domain.model.user.User
import org.springframework.stereotype.Service

@Service
class GetMfaEnabled {
    fun execute(user: User): Boolean {
        return user.security.mfaEnabled
    }
}