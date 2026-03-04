package health.kokoro.application.bean

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock

@Configuration
class SimpleBeans {
    @Bean
    fun clock(): Clock = Clock.systemDefaultZone()

}