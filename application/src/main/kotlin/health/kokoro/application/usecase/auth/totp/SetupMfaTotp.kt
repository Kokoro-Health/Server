package health.kokoro.application.usecase.auth.totp

import dev.samstevens.totp.qr.QrDataFactory
import dev.samstevens.totp.qr.QrGenerator
import dev.samstevens.totp.secret.SecretGenerator
import health.kokoro.domain.error.MfaAlreadyEnabledException
import health.kokoro.domain.model.user.User
import health.kokoro.domain.port.user.UserSecurityRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class SetupMfaTotp(
    private val secretGen: SecretGenerator,
    private val qrFactory: QrDataFactory,
    private val qrGenerator: QrGenerator,
    private val securityRepository: UserSecurityRepository
) {
    fun execute(user: User): Response {
        if (user.security.mfaEnabled) {
            throw MfaAlreadyEnabledException()
        }

        val secret = secretGen.generate()
        val qrData = qrFactory.newBuilder()
            .label(user.email)
            .secret(secret)
            .issuer("Kokoro")
            .build()

        val qrImage = qrGenerator.generate(qrData)
        val qrBase64 = Base64.getEncoder().encodeToString(qrImage)

        securityRepository.update(user, secret)

        return Response(secret, qrBase64)
    }

    data class Response(val secret: String, val qrCodeBase64: String)
}