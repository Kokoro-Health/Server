package health.kokoro.application.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties("kokoro.cors")
@Configuration
class CorsConfig {
    var allowOrigin: List<String> = listOf()
}