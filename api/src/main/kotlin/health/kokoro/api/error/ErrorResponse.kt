package health.kokoro.api.error

import io.swagger.v3.oas.annotations.media.Schema


@Schema(name = "ErrorResponse", description = "Standard error structure")
data class ErrorResponse(val message: String)