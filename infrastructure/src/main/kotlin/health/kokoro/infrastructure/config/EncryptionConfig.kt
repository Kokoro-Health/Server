package health.kokoro.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "kokoro.encryption")
class EncryptionConfig {
    var provider: String = "local"
    var vaultUrl: String = "http://localhost:8200"
    var vaultToken: String = ""
    var vaultPath: String = "secret/kokoro"
}
