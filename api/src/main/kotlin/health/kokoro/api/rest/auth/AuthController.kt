package health.kokoro.api.rest.auth

import health.kokoro.application.usecase.auth.ResetPassword
import health.kokoro.application.usecase.auth.SignIn
import health.kokoro.application.usecase.auth.SignUp
import health.kokoro.domain.model.user.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
@Validated
@Tag(name = "Authentication", description = "User authentication endpoints")
class AuthController(
    private val signUp: SignUp,
    private val signIn: SignIn,
    private val mapper: AuthDtoMapper,
    private val resetPassword: ResetPassword
) {
    @PostMapping("/signup")
    @Operation(summary = "Register new user")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "User created"),
        ApiResponse(responseCode = "400", description = "Validation failed"),
        ApiResponse(responseCode = "409", description = "Email already exists")
    )
    fun signUp(@RequestBody @Valid req: SignUpRequestDto): ResponseEntity<Unit> {
        val result = signUp.execute(mapper.toCommand(req))
        val cookie = mapper.toCookie(AuthTokenResponseDto(result.token, result.expiresIn), false)

        return ResponseEntity.status(HttpStatus.CREATED)
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .build()
    }

    @PostMapping("/signin")
    @Operation(summary = "Sign in with email and password")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Sign in successful"),
        ApiResponse(responseCode = "401", description = "Invalid credentials or MFA required"),
        ApiResponse(responseCode = "400", description = "Validation failed")
    )
    fun signIn(
        @RequestBody @Valid req: SignInRequestDto,
        request: HttpServletRequest
    ): ResponseEntity<SignInResponseDto> {
        val result = signIn.execute(mapper.toCommand(req), request)

        if (result.mfaRequired) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(SignInResponseDto(mfaRequired = true))
        }

        val cookie = mapper.toCookie(
            AuthTokenResponseDto(result.token!!, result.expiresIn!!),
            req.rememberMe
        )

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(SignInResponseDto(mfaRequired = false))
    }

    @PostMapping("/logout")
    @Operation(summary = "Sign out current session")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Successfully logged out")
    )
    fun logout(): ResponseEntity<Any> {
        val cookie = mapper.toDeletionCookie()

        return ResponseEntity.noContent()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .build()
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Request password reset", description = "Sends reset code to email")
    @ApiResponses(
        ApiResponse(responseCode = "202", description = "Reset code sent"),
        ApiResponse(responseCode = "400", description = "Invalid email")
    )
    fun requestPasswordReset(
        @Parameter(description = "Email address", example = "john.doe@example.com")
        @RequestParam @Email @Valid email: String
    ): ResponseEntity<Any> {
        resetPassword.execute(email)
        return ResponseEntity.accepted().build()
    }

    @PostMapping("/reset-password/confirm")
    @Operation(summary = "Confirm password reset with code")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Password reset successful"),
        ApiResponse(responseCode = "400", description = "Invalid or expired code"),
        ApiResponse(responseCode = "401", description = "Unauthorized")
    )
    fun resetPassword(
        @RequestBody @Valid req: PasswordResetRequestDto,
        @AuthenticationPrincipal user: User,
        request: HttpServletRequest
    ): ResponseEntity<Any> {
        resetPassword.execute(user, req.code, req.password, request)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/validate-code")
    @Operation(summary = "Validate password reset code", description = "Returns 204 if code is valid")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Code is valid"),
        ApiResponse(responseCode = "410", description = "Code expired")
    )
    fun validatePasswordResetCode(
        @Parameter(description = "Reset code", example = "123456")
        @RequestParam code: String
    ): ResponseEntity<Any> {
        resetPassword.validateCode(code)
        return ResponseEntity.noContent().build()
    }
}
