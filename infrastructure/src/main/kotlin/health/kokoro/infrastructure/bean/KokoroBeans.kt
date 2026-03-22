package health.kokoro.infrastructure.bean

import health.kokoro.infrastructure.config.MailConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl

@Configuration
class KokoroBeans(
    private val mailConfig: MailConfig
) {
    @Bean
    fun getMailSender(): JavaMailSender {
        val sender = JavaMailSenderImpl()
        sender.host = mailConfig.host
        sender.port = mailConfig.port
        sender.username = mailConfig.username
        sender.password = mailConfig.password
        sender.defaultEncoding = "UTF-8"
        return sender
    }
}