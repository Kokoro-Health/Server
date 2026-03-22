package health.kokoro.domain.port.security

import javax.crypto.SecretKey

interface KeyProvider {
    fun getCurrentKeyId(): String
    fun getKey(id: String): SecretKey
}