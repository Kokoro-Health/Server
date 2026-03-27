package health.kokoro.application.security.passkey

import com.yubico.webauthn.CredentialRepository
import com.yubico.webauthn.RegisteredCredential
import com.yubico.webauthn.data.ByteArray
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor
import health.kokoro.domain.port.user.UserRepository
import health.kokoro.domain.port.user.passkey.PasskeyRepository
import org.springframework.stereotype.Component
import java.util.Optional
import java.util.UUID

@Component
class PasskeyCredentialRepository(
    private val repo: PasskeyRepository,
    private val userRepo: UserRepository
): CredentialRepository {
    override fun getCredentialIdsForUsername(username: String): Set<PublicKeyCredentialDescriptor?> {
               return repo.findByEmail(username).map {
            PublicKeyCredentialDescriptor.builder()
                .id(ByteArray.fromBase64Url(it.credentialId))
                .build()
        }.toSet()
    }

    override fun getUserHandleForUsername(username: String): Optional<ByteArray> {
                val user = userRepo.findByEmail(username) ?: return Optional.empty()
        return Optional.of(ByteArray.fromBase64Url(user.id.toString()))
    }

    override fun getUsernameForUserHandle(userHandle: ByteArray): Optional<String> {
                val user = userRepo.findById(UUID.fromString(userHandle.base64Url)) ?: return Optional.empty()
        return Optional.of(user.email)
    }

    override fun lookup(
        credentialId: ByteArray,
        userHandle: ByteArray
    ): Optional<RegisteredCredential> {
                val passkey = repo.findByCredentialId(credentialId.base64Url) ?: return Optional.empty()
        return Optional.of(
            RegisteredCredential.builder()
                .credentialId(credentialId)
                .userHandle(userHandle)
                .publicKeyCose(ByteArray(passkey.publicKey))
                .signatureCount(passkey.signCount)
                .build()
        )
    }

    override fun lookupAll(credentialId: ByteArray): Set<RegisteredCredential> {
                val passkey = repo.findByCredentialId(credentialId.base64Url) ?: return emptySet()
        return setOf(
            RegisteredCredential.builder()
                .credentialId(credentialId)
                .userHandle(ByteArray.fromBase64Url(passkey.userId.toString()))
                .publicKeyCose(ByteArray(passkey.publicKey))
                .signatureCount(passkey.signCount)
                .build()
        )
    }
}