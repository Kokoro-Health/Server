package health.kokoro.api.error

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant


@Schema(name = "ErrorResponseDto", description = "Standard error structure")
data class ErrorResponseDto(
    val code: String,
    val message: String,
    val timestamp: Instant = Instant.now()
)
