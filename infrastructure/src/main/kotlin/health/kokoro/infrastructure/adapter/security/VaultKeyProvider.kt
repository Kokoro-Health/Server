package health.kokoro.infrastructure.adapter.security

import health.kokoro.domain.port.security.KeyProvider
import health.kokoro.infrastructure.config.VaultConfig
import io.github.jopenlibs.vault.Vault
import org.springframework.stereotype.Repository
import java.util.Base64
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Repository
class VaultKeyProvider(
    private val vault: Vault,
    private val config: VaultConfig
) : KeyProvider {

    override fun getCurrentKeyId(): String = "key-v1"

    override fun getKey(id: String): SecretKey {
        val fullKeyPath = config.keyPath + "/" + id
        var keyData: String

        try {
            val response = vault.logical().read(fullKeyPath)
            val data = response?.data
            keyData = data?.get("key")
                ?: generateAndStoreKey(fullKeyPath)
        } catch (_: Exception) {
            keyData = generateAndStoreKey(fullKeyPath)
        }

        val keyBytes = Base64.getDecoder().decode(keyData)
        return SecretKeySpec(keyBytes, "AES")
    }

    private fun generateAndStoreKey(fullKeyPath: String): String {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256)
        val secretKey = keyGen.generateKey()
        val encodedKey = Base64.getEncoder().encodeToString(secretKey.encoded)

        vault.logical().write(fullKeyPath, mapOf("key" to encodedKey))
        return encodedKey
    }
}