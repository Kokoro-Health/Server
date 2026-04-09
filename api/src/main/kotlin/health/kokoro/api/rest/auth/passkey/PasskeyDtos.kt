package health.kokoro.api.rest.auth.passkey

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.Instant
import java.util.*

data class RegisterPasskeyStartResponseDto(
    val options: String
)

data class RegisterPasskeyFinishRequestDto(
    @field:NotBlank
    val credential: String,
    @field:Size(max = 100)
    val deviceName: String
)

data class RegisterPasskeyFinishResponseDto(
    val id: UUID,
    val deviceName: String?,
    val createdAt: Instant
)

data class AuthPasskeyStartRequestDto(
    @field:NotBlank
    @field:Email
    val email: String
)

data class AuthPasskeyStartResponseDto(
    val options: String
)

data class AuthPasskeyFinishRequestDto(
    @field:NotBlank
    @field:Email
    val email: String,

    @field:NotBlank
    val credential: String
)

data class AuthPasskeyFinishResponseDto(
    val token: String
)

data class PasskeyResponseDto(
    val id: UUID,
    val deviceName: String?,
    val createdAt: Instant,
    val lastUsedAt: Instant?
)
