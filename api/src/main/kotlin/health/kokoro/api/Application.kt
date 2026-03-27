package health.kokoro.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@ComponentScan(basePackages = ["health.kokoro"])
@EnableConfigurationProperties
@EnableJpaRepositories(basePackages = ["health.kokoro.infrastructure.jpa"])
@EntityScan(basePackages = ["health.kokoro.infrastructure.jpa"])
@EnableJpaAuditing
@EnableScheduling
class KokoroRunner

fun main(args: Array<String>) {
    runApplication<KokoroRunner>(*args)
}
