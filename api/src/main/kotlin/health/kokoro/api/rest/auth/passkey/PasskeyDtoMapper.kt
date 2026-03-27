package health.kokoro.api.rest.auth.passkey

import health.kokoro.domain.model.user.security.passkey.Passkey
import org.springframework.stereotype.Component

@Component
class PasskeyDtoMapper {
    fun toPasskeyResponse(passkey: Passkey): PasskeyResponse =
        PasskeyResponse(
            id = passkey.id,
            deviceName = passkey.deviceName,
            createdAt = passkey.createdAt,
            lastUsedAt = passkey.lastUsedAt
        )

    fun toRegisterFinishResponse(passkey: Passkey): RegisterPasskeyFinishResponse =
        RegisterPasskeyFinishResponse(
            id = passkey.id,
            deviceName = passkey.deviceName,
            createdAt = passkey.createdAt
        )
}
