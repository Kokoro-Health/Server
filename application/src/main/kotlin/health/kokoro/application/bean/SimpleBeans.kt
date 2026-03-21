package health.kokoro.application.bean

import dev.samstevens.totp.code.CodeVerifier
import dev.samstevens.totp.code.DefaultCodeGenerator
import dev.samstevens.totp.code.DefaultCodeVerifier
import dev.samstevens.totp.code.HashingAlgorithm
import dev.samstevens.totp.qr.QrDataFactory
import dev.samstevens.totp.qr.QrGenerator
import dev.samstevens.totp.qr.ZxingPngQrGenerator
import dev.samstevens.totp.secret.DefaultSecretGenerator
import dev.samstevens.totp.secret.SecretGenerator
import dev.samstevens.totp.time.SystemTimeProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.security.SecureRandom

@Configuration
class SimpleBeans {
    @Bean
    fun secretGenerator(): SecretGenerator = DefaultSecretGenerator()

    @Bean
    fun qrDataFactory(): QrDataFactory = QrDataFactory(
        HashingAlgorithm.SHA1,
        6,
        30
    )

    @Bean
    fun qrGenerator(): QrGenerator = ZxingPngQrGenerator()

    @Bean
    fun codeVerifier(): CodeVerifier = DefaultCodeVerifier(
        DefaultCodeGenerator(HashingAlgorithm.SHA1),
        SystemTimeProvider()
    )

    @Bean
    fun secureRandom() = SecureRandom()
}