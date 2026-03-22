package health.kokoro.domain.port.security

import health.kokoro.domain.model.security.EncryptedData

interface EncryptionPort {
    fun encrypt(plainText: String): EncryptedData
    fun decrypt(encryptedData: EncryptedData): String
}