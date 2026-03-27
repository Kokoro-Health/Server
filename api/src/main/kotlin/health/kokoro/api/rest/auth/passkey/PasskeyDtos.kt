package health.kokoro.api.rest.auth.passkey

import java.time.Instant
import java.util.UUID

data class RegisterPasskeyStartResponse(
    val options: String
)

data class RegisterPasskeyFinishRequest(
    val credential: String,
    val deviceName: String
)

data class RegisterPasskeyFinishResponse(
    val id: UUID,
    val deviceName: String?,
    val createdAt: Instant
)

data class AuthPasskeyStartRequest(
    val email: String
)

data class AuthPasskeyStartResponse(
    val options: String
)

data class AuthPasskeyFinishRequest(
    val email: String,
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
