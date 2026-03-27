package health.kokoro.domain.port.user.passkey

import health.kokoro.domain.model.user.security.passkey.Passkey
import java.util.*

interface PasskeyRepository {
    fun findByUserId(userId: UUID): List<Passkey>
    fun findByCredentialId(credentialId: String): Passkey?
    fun findByEmail(email: String): List<Passkey>
    fun save(dto: Passkey): Passkey
    fun findById(id: UUID): Passkey?
    fun delete(dto: Passkey)
}