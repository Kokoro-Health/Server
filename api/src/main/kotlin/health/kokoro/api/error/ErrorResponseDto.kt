package health.kokoro.api.error

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant


@Schema(name = "ErrorResponseDto", description = "Standard error structure")
data class ErrorResponseDto(val message: String, val timeStamp: Instant)