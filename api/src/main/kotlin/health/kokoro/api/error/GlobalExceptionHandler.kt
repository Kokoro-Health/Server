package health.kokoro.api.error

import health.kokoro.domain.error.FeatureNotEnabledException
import health.kokoro.domain.error.InvalidCredentialsException
import org.springframework.http.HttpStatus
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

    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentialsException(e: InvalidCredentialsException): ResponseEntity<ErrorResponseDto> {
        return ResponseEntity.badRequest().body(ErrorResponseDto("Invalid Credentials", Instant.now()))
    }

    @ExceptionHandler(FeatureNotEnabledException::class)
    fun handleFeatureNotEnabledException(e: FeatureNotEnabledException): ResponseEntity<ErrorResponseDto> {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErrorResponseDto("You haven't enabled this feature."))
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElement(e: NoSuchElementException): ResponseEntity<ErrorResponseDto> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponseDto(e.message ?: "Resource not found", Instant.now()))
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(e: AccessDeniedException): ResponseEntity<ErrorResponseDto> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ErrorResponseDto(e.message ?: "Access denied", Instant.now()))
    }

    @ExceptionHandler(SecurityException::class)
    fun handleSecurityException(e: SecurityException): ResponseEntity<ErrorResponseDto> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ErrorResponseDto(e.message ?: "Security violation", Instant.now()))
    }
}