package health.kokoro.application.bean

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.time.Clock

@Configuration
class SimpleBeans {
    @Bean
    fun clock(): Clock = Clock.systemDefaultZone()

}