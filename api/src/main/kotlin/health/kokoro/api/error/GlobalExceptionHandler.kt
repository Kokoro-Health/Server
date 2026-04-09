package health.kokoro.api.error

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.resource.NoResourceFoundException

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponseDto> {
        val fieldErrors = ex.bindingResult.fieldErrors
        if (fieldErrors.isNotEmpty()) {
            return ResponseEntity.badRequest().body(ErrorResponseDto(fieldErrors[0].defaultMessage!!))
        }
        return ResponseEntity.badRequest().body(ErrorResponseDto(ex.body.title.toString()))
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleResourceNotFoundException(): ResponseEntity<ErrorResponseDto> {
        return ResponseEntity.notFound().build()
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<ErrorResponseDto> {
        return ResponseEntity.badRequest().body(ErrorResponseDto(e.message ?: "Bad request"))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResponseDto> {
        return ResponseEntity.badRequest().body(ErrorResponseDto(e.message ?: "Internal server error"))
    }
}