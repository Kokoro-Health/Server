package health.kokoro.infrastructure.adapter.security

import health.kokoro.domain.port.security.KeyProvider
import health.kokoro.infrastructure.config.EncryptionConfig
import org.springframework.stereotype.Service
import java.io.File
import java.util.*
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Service
class LocalKeyProvider(
    config: EncryptionConfig
) : KeyProvider {

    private val keyDirectory: File = File(config.keyStorePath)

    init {
        keyDirectory.mkdirs()
    }

    override fun getCurrentKeyId(): String = "key-v1"

    override fun getKey(id: String): SecretKey {
        val keyFile = getKeyFile(id)

        if (!keyFile.exists()) {
            generateAndStoreKey(id)
        }

        val encodedKey = keyFile.readText(Charsets.UTF_8)
        val keyBytes = Base64.getDecoder().decode(encodedKey)
        return SecretKeySpec(keyBytes, "AES")
    }

    private fun generateAndStoreKey(id: String) {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256)
        val secretKey = keyGen.generateKey()
        val encodedKey = Base64.getEncoder().encodeToString(secretKey.encoded)

        val keyFile = getKeyFile(id)
        keyFile.writeText(encodedKey, Charsets.UTF_8)
        keyFile.setReadable(true, true)
        keyFile.setWritable(true, true)
    }

    private fun getKeyFile(id: String): File {
        return File(keyDirectory, "$id.key")
    }
}
