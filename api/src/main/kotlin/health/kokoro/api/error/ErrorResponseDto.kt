package health.kokoro.api.error

import io.swagger.v3.oas.annotations.media.Schema


@Schema(name = "ErrorResponseDto", description = "Standard error structure")
data class ErrorResponseDto(val message: String)