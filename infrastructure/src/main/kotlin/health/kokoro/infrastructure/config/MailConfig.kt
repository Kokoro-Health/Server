package health.kokoro.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "spring.mail")
class MailConfig {
    var host: String = ""
    var port: Int = 0
    var username: String = ""
    var password: String = ""
}