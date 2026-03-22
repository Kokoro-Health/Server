package health.kokoro.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "kokoro.encryption")
class EncryptionConfig {
    var keyStorePath: String = "./keys"
}
