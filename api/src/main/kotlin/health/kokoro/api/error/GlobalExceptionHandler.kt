package health.kokoro.api.error

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.resource.NoResourceFoundException
import java.time.Instant
import java.util.logging.Level
import java.util.logging.Logger

@ControllerAdvice
class GlobalExceptionHandler {
    companion object {
        private val logger: Logger = Logger.getLogger(this::javaClass.name)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponseDto> {
        val fieldErrors = ex.bindingResult.fieldErrors
        if (fieldErrors.isNotEmpty()) {
            return ResponseEntity.badRequest().body(ErrorResponseDto(fieldErrors[0].defaultMessage!!, Instant.now()))
        }
        return ResponseEntity.badRequest().body(ErrorResponseDto(ex.body.title.toString(), Instant.now()))
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleResourceNotFoundException(): ResponseEntity<ErrorResponseDto> {
        return ResponseEntity.notFound().build()
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<ErrorResponseDto> {
        return ResponseEntity.badRequest().body(ErrorResponseDto(e.message ?: "Bad request", Instant.now()))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResponseDto> {
        logger.log(Level.SEVERE, "Internal Server error: " + e.message)
        e.printStackTrace()
        return ResponseEntity.internalServerError()
            .body(ErrorResponseDto(e.message ?: "Internal server error", Instant.now()))
    }
}