package health.kokoro.infrastructure.adapter.security

import health.kokoro.domain.port.security.KeyProvider
import health.kokoro.infrastructure.config.EncryptionConfig
import io.github.jopenlibs.vault.Vault
import io.github.jopenlibs.vault.VaultConfig
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Service
class HashiCorpVaultKeyProvider(
    private val config: EncryptionConfig
) : KeyProvider {

    private val vault: Vault by lazy {
        val vaultConfig = VaultConfig()
            .address(config.vaultUrl)
            .token(config.vaultToken)
            .build()
        Vault.create(vaultConfig)
    }

    override fun getCurrentKeyId(): String = "key-v1"

    override fun getKey(id: String): SecretKey {
        val path = "${config.vaultPath}/$id"
        
        return try {
            val response = vault.logical().read(path)
            if (response.restResponse.status == 404 || (response.restResponse.status == 200 && response.data["key"] == null)) {
                val newKey = generateKey()
                storeKey(id, newKey)
                newKey
            } else if (response.restResponse.status == 200) {
                val encodedKey = response.data["key"] ?: throw RuntimeException("Key not found in Vault at $path")
                val keyBytes = Base64.getDecoder().decode(encodedKey)
                SecretKeySpec(keyBytes, "AES")
            } else {
                throw RuntimeException("Error reading key from Vault at $path: status ${response.restResponse.status}, ${response.restResponse.body?.let { String(it) }}")
            }
        } catch (e: Exception) {
            if (e is RuntimeException && e.message?.contains("Error reading key") == true) {
                throw e
            }
            throw RuntimeException("Failed to retrieve key from Vault at $path", e)
        }
    }

    private fun generateKey(): SecretKey {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256)
        return keyGen.generateKey()
    }

    private fun storeKey(id: String, key: SecretKey) {
        val path = "${config.vaultPath}/$id"
        val encodedKey = Base64.getEncoder().encodeToString(key.encoded)
        val data = mapOf("key" to encodedKey)
        
        val response = vault.logical().write(path, data)
        if (response.restResponse.status !in 200..204) {
            throw RuntimeException("Failed to store key in Vault at $path: ${response.restResponse.body?.let { String(it) }}")
        }
    }
}