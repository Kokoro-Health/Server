package health.kokoro.infrastructure.adapter.security

import health.kokoro.domain.model.security.EncryptedData
import health.kokoro.domain.port.security.EncryptionPort
import health.kokoro.domain.port.security.KeyProvider
import org.springframework.stereotype.Repository
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec

@Repository
class AesGcmEncryptionService(
    private val keyProvider: KeyProvider
) : EncryptionPort {
    override fun encrypt(plainText: String): EncryptedData {
        val keyId = keyProvider.getCurrentKeyId()
        val secretKey = keyProvider.getKey(keyId)

        val iv = ByteArray(12).also { SecureRandom().nextBytes(it) }

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(128, iv))

        val ciphertext = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))

        return EncryptedData(ciphertext, iv, keyId)
    }

    override fun decrypt(encryptedData: EncryptedData): String {
        val secretKey = keyProvider.getKey(encryptedData.keyId)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, encryptedData.initializationVector))

        val plaintext = cipher.doFinal(encryptedData.cipherText)
        return String(plaintext, Charsets.UTF_8)
    }
}