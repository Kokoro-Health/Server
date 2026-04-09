package health.kokoro.api.error

import health.kokoro.domain.error.*
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.resource.NoResourceFoundException
import java.time.Instant
import java.util.logging.Level
import java.util.logging.Logger

@RestControllerAdvice
class GlobalExceptionHandler {
    companion object {
        private val logger: Logger = Logger.getLogger(this::javaClass.name)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponseDto> {
        val fieldErrors = ex.bindingResult.fieldErrors
        if (fieldErrors.isNotEmpty()) {
            return ResponseEntity.badRequest()
                .body(
                    ErrorResponseDto(
                        code = "VALIDATION_ERROR",
                        message = fieldErrors[0].defaultMessage ?: "Validation failed",
                        timestamp = Instant.now()
                    )
                )
        }
        return ResponseEntity.badRequest()
            .body(
                ErrorResponseDto(
                    code = "VALIDATION_ERROR",
                    message = ex.body.title.toString(),
                    timestamp = Instant.now()
                )
            )
    }

    @ExceptionHandler(NoResourceFoundException::class)
    fun handleResourceNotFoundException(): ResponseEntity<ErrorResponseDto> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                ErrorResponseDto(
                    code = "RESOURCE_NOT_FOUND",
                    message = "Resource not found",
                    timestamp = Instant.now()
                )
            )
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<ErrorResponseDto> {
        return ResponseEntity.badRequest()
            .body(
                ErrorResponseDto(
                    code = "BAD_REQUEST",
                    message = e.message ?: "Bad request",
                    timestamp = Instant.now()
                )
            )
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentials(e: BadCredentialsException): ResponseEntity<ErrorResponseDto> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(
                ErrorResponseDto(
                    code = "INVALID_CREDENTIALS",
                    message = "Invalid credentials",
                    timestamp = Instant.now()
                )
            )
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(e: AuthenticationException): ResponseEntity<ErrorResponseDto> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(
                ErrorResponseDto(
                    code = "AUTHENTICATION_FAILED",
                    message = "Authentication failed",
                    timestamp = Instant.now()
                )
            )
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(e: UserNotFoundException): ResponseEntity<ErrorResponseDto> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                ErrorResponseDto(
                    code = "USER_NOT_FOUND",
                    message = e.message ?: "User not found",
                    timestamp = Instant.now()
                )
            )
    }

    @ExceptionHandler(EmailAlreadyExistsException::class)
    fun handleEmailAlreadyExists(e: EmailAlreadyExistsException): ResponseEntity<ErrorResponseDto> {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(
                ErrorResponseDto(
                    code = "EMAIL_ALREADY_EXISTS",
                    message = e.message ?: "Email already in use",
                    timestamp = Instant.now()
                )
            )
    }

    @ExceptionHandler(EmailAlreadyTakenException::class)
    fun handleEmailAlreadyTaken(e: EmailAlreadyTakenException): ResponseEntity<ErrorResponseDto> {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(
                ErrorResponseDto(
                    code = "EMAIL_ALREADY_TAKEN",
                    message = e.message ?: "E-Mail is already taken",
                    timestamp = Instant.now()
                )
            )
    }

    @ExceptionHandler(MfaNotSetupException::class)
    fun handleMfaNotSetup(e: MfaNotSetupException): ResponseEntity<ErrorResponseDto> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponseDto(
                    code = "MFA_NOT_SETUP",
                    message = e.message ?: "MFA is not set up",
                    timestamp = Instant.now()
                )
            )
    }

    @ExceptionHandler(MfaAlreadyEnabledException::class)
    fun handleMfaAlreadyEnabled(e: MfaAlreadyEnabledException): ResponseEntity<ErrorResponseDto> {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(
                ErrorResponseDto(
                    code = "MFA_ALREADY_ENABLED",
                    message = e.message ?: "MFA is already enabled",
                    timestamp = Instant.now()
                )
            )
    }

    @ExceptionHandler(MfaNotEnabledException::class)
    fun handleMfaNotEnabled(e: MfaNotEnabledException): ResponseEntity<ErrorResponseDto> {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(
                ErrorResponseDto(
                    code = "MFA_NOT_ENABLED",
                    message = e.message ?: "MFA is not enabled",
                    timestamp = Instant.now()
                )
            )
    }

    @ExceptionHandler(
        InvalidMfaCodeException::class,
        InvalidVerificationCodeException::class,
        InvalidPasswordException::class
    )
    fun handleInvalidCode(e: KokoroException): ResponseEntity<ErrorResponseDto> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponseDto(
                    code = "INVALID_CODE",
                    message = e.message ?: "Invalid code",
                    timestamp = Instant.now()
                )
            )
    }

    @ExceptionHandler(CodeExpiredException::class, ChallengeExpiredException::class)
    fun handleExpiredCode(e: KokoroException): ResponseEntity<ErrorResponseDto> {
        return ResponseEntity.status(HttpStatus.GONE)
            .body(
                ErrorResponseDto(
                    code = "CODE_EXPIRED",
                    message = e.message ?: "Code has expired",
                    timestamp = Instant.now()
                )
            )
    }

    @ExceptionHandler(ChallengeNotFoundException::class, NoActiveChallengeException::class)
    fun handleChallengeNotFound(e: KokoroException): ResponseEntity<ErrorResponseDto> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                ErrorResponseDto(
                    code = "CHALLENGE_NOT_FOUND",
                    message = e.message ?: "No active challenge found",
                    timestamp = Instant.now()
                )
            )
    }

    @ExceptionHandler(CredentialNotFoundException::class)
    fun handleCredentialNotFound(e: CredentialNotFoundException): ResponseEntity<ErrorResponseDto> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                ErrorResponseDto(
                    code = "CREDENTIAL_NOT_FOUND",
                    message = e.message ?: "Credential not found",
                    timestamp = Instant.now()
                )
            )
    }

    @ExceptionHandler(PasskeyNotFoundException::class)
    fun handlePasskeyNotFound(e: PasskeyNotFoundException): ResponseEntity<ErrorResponseDto> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                ErrorResponseDto(
                    code = "PASSKEY_NOT_FOUND",
                    message = e.message ?: "Passkey not found",
                    timestamp = Instant.now()
                )
            )
    }

    @ExceptionHandler(PasskeyOwnershipException::class)
    fun handlePasskeyOwnership(e: PasskeyOwnershipException): ResponseEntity<ErrorResponseDto> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(
                ErrorResponseDto(
                    code = "ACCESS_DENIED",
                    message = e.message ?: "Access denied",
                    timestamp = Instant.now()
                )
            )
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException::class)
    fun handleAccessDenied(e: org.springframework.security.access.AccessDeniedException): ResponseEntity<ErrorResponseDto> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(
                ErrorResponseDto(
                    code = "ACCESS_DENIED",
                    message = e.message ?: "Access denied",
                    timestamp = Instant.now()
                )
            )
    }

    @ExceptionHandler(JournalEntryLockedException::class)
    fun handleJournalLocked(e: JournalEntryLockedException): ResponseEntity<ErrorResponseDto> {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(
                ErrorResponseDto(
                    code = "JOURNAL_LOCKED",
                    message = e.message ?: "Journal entry is locked",
                    timestamp = Instant.now()
                )
            )
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElement(e: NoSuchElementException): ResponseEntity<ErrorResponseDto> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(
                ErrorResponseDto(
                    code = "RESOURCE_NOT_FOUND",
                    message = e.message ?: "Resource not found",
                    timestamp = Instant.now()
                )
            )
    }

    @ExceptionHandler(SamePasswordException::class)
    fun handleSamePassword(e: SamePasswordException): ResponseEntity<ErrorResponseDto> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponseDto(
                    code = "SAME_PASSWORD",
                    message = e.message ?: "New password cannot be the same as the old one",
                    timestamp = Instant.now()
                )
            )
    }

    @ExceptionHandler(SecurityException::class)
    fun handleSecurityException(e: SecurityException): ResponseEntity<ErrorResponseDto> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(
                ErrorResponseDto(
                    code = "SECURITY_VIOLATION",
                    message = e.message ?: "Security violation",
                    timestamp = Instant.now()
                )
            )
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ErrorResponseDto> {
        logger.log(Level.SEVERE, "Internal Server error: " + e.message)
        e.printStackTrace()
        return ResponseEntity.internalServerError()
            .body(
                ErrorResponseDto(
                    code = "INTERNAL_ERROR",
                    message = e.message ?: "Internal server error",
                    timestamp = Instant.now()
                )
            )
    }
}
