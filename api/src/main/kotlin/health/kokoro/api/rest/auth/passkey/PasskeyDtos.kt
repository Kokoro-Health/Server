package health.kokoro.api.rest.auth.passkey

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.Instant
import java.util.*

data class RegisterPasskeyStartResponseDto(
    @field:Schema(description = "WebAuthn registration options (JSON)")
    val options: String
)

data class RegisterPasskeyFinishRequestDto(
    @field:NotBlank(message = "Credential is required")
    @field:Schema(description = "WebAuthn credential response")
    val credential: String,
    @field:Size(max = 100, message = "Device name too long")
    @field:Schema(description = "Friendly name for device", example = "MacBook Pro")
    val deviceName: String
)

data class RegisterPasskeyFinishResponseDto(
    @field:Schema(description = "Passkey ID")
    val id: UUID,
    @field:Schema(description = "Device name", nullable = true)
    val deviceName: String?,
    @field:Schema(description = "Creation timestamp", example = "1704067200000")
    val createdAt: Instant
)

data class AuthPasskeyStartRequestDto(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email")
    @field:Schema(description = "User email", example = "john.doe@example.com")
    val email: String
)

data class AuthPasskeyStartResponseDto(
    @field:Schema(description = "WebAuthn authentication options (JSON)")
    val options: String
)

data class AuthPasskeyFinishRequestDto(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email")
    @field:Schema(description = "User email", example = "john.doe@example.com")
    val email: String,
    @field:NotBlank(message = "Credential is required")
    @field:Schema(description = "WebAuthn assertion response")
    val credential: String
)

data class AuthPasskeyFinishResponseDto(
    @field:Schema(description = "JWT access token")
    val token: String
)

data class PasskeyResponseDto(
    @field:Schema(description = "Passkey ID")
    val id: UUID,
    @field:Schema(description = "Device name", nullable = true, example = "MacBook Pro")
    val deviceName: String?,
    @field:Schema(description = "Creation timestamp", example = "1704067200000")
    val createdAt: Instant,
    @field:Schema(description = "Last used timestamp", nullable = true, example = "1704153600000")
    val lastUsedAt: Instant?
)
