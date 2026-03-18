package health.kokoro.api.rest.user.profile

import java.time.Instant

data class VerificationRequestResponseDto(
    val nextCodeAllowedAt: Instant
)
