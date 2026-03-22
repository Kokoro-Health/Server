package health.kokoro.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "kokoro.vault")
class VaultConfig {
   var uri: String = ""
   var token: String = ""
   var keyPath: String = "secret/data/kokoro/encryption-keys"
   var ssl: Boolean = false
}
