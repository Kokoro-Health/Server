package health.kokoro.api.rest.auth.mfa

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class SetupMfaResponse(
    val secret: String,
    val qrCodeBase64: String
)

data class VerifyMfaRequest(
    @field:NotBlank(message = "Code is required")
    @field:Size(min = 6, max = 6, message = "Code must be 6 digits")
    val code: String
)

data class DisableMfaRequest(
    @field:NotBlank(message = "Password is required")
    val password: String
)

data class MfaSettings(
    val mfaEnabled: Boolean
)