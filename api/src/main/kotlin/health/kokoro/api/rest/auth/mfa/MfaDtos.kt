package health.kokoro.api.rest.auth.mfa

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class SetupMfaResponseDto(
    @field:Schema(description = "TOTP secret (only shown once)")
    val secret: String,
    @field:Schema(description = "QR code as base64 PNG")
    val qrCodeBase64: String
)

data class VerifyMfaRequestDto(
    @field:NotBlank(message = "Code is required")
    @field:Size(min = 6, max = 6, message = "Code must be 6 digits")
    @field:Schema(description = "6-digit TOTP code", example = "123456")
    val code: String
)

data class DisableMfaRequestDto(
    @field:NotBlank(message = "Password is required")
    @field:Schema(description = "Current password for verification")
    val password: String
)

data class MfaSettingsResponseDto(
    @field:Schema(description = "Whether MFA is currently enabled")
    val mfaEnabled: Boolean
)
