package health.kokoro.api.error

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.resource.NoResourceFoundException

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val fieldErrors = ex.bindingResult.fieldErrors
        if (fieldErrors.isNotEmpty()) {
            return ResponseEntity.badRequest().body(ErrorResponse(fieldErrors[0].defaultMessage!!))
        }
        return ResponseEntity.badRequest().body(ErrorResponse(ex.body.title.toString()))
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleResourceNotFoundException(): ResponseEntity<ErrorResponse> {
        return ResponseEntity.notFound().build()
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.badRequest().body(ErrorResponse(e.message ?: "Bad request"))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResponse> {
        return ResponseEntity.badRequest().body(ErrorResponse(e.message ?: "Internal server error"))
    }
}