package health.kokoro.infrastructure.bean

import ch.qos.logback.core.util.TimeUtil
import health.kokoro.infrastructure.config.MailConfig
import health.kokoro.infrastructure.config.VaultConfig
import io.github.jopenlibs.vault.SslConfig
import io.github.jopenlibs.vault.Vault
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.concurrent.TimeUnit

@Configuration

class KokoroBeans(
    private val mailConfig: MailConfig,
    private val vaultConfig: VaultConfig
) {
    @Bean
    fun getVaultClient(): Vault {
       return Vault.create(
            io.github.jopenlibs.vault.VaultConfig()
                .token(vaultConfig.token)
                .address(vaultConfig.uri)
                .readTimeout(10_000)
                .prefixPath(vaultConfig.keyPath)
                .sslConfig(SslConfig().verify(vaultConfig.ssl))
        )
    }

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