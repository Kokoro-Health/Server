package health.kokoro.application.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "kokoro.jwt")
class JwtConfig {
    lateinit var secret: String
    lateinit var issuer: String
    var expiration: Long = 0
}