package health.kokoro.api.rest.auth.passkey

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.Instant
import java.util.*

data class RegisterPasskeyStartResponse(
    val options: String
)

data class RegisterPasskeyFinishRequest(
    @field:NotBlank
    val credential: String,
    @field:Size(max = 100)
    val deviceName: String
)

data class RegisterPasskeyFinishResponse(
    val id: UUID,
    val deviceName: String?,
    val createdAt: Instant
)

data class AuthPasskeyStartRequest(
    @field:NotBlank
    @field:Email
    val email: String
)

data class AuthPasskeyStartResponse(
    val options: String
)

data class AuthPasskeyFinishRequest(
    @field:NotBlank
    @field:Email
    val email: String,

    @field:NotBlank
    val credential: String
)

data class AuthPasskeyFinishResponse(
    val token: String
)

data class PasskeyResponse(
    val id: UUID,
    val deviceName: String?,
    val createdAt: Instant,
    val lastUsedAt: Instant?
)
