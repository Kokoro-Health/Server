package health.kokoro.api.rest.auth

import health.kokoro.application.usecase.auth.ResetPassword
import health.kokoro.application.usecase.auth.SignIn
import health.kokoro.application.usecase.auth.SignUp
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
@Validated
class AuthController(
    private val signUp: SignUp,
    private val signIn: SignIn,
    private val mapper: AuthDtoMapper,
    private val resetPassword: ResetPassword
) {
    @PostMapping("/signup")
    fun signUp(@RequestBody @Valid req: SignUpRequestDto): ResponseEntity<Unit> {
        val result = signUp.execute(mapper.toCommand(req))
        val cookie = mapper.toCookie(AuthTokenResponseDto(result.token, result.expiresIngit), false)

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .build()
    }

    @PostMapping("/signin")
    fun signIn(@RequestBody @Valid req: SignInRequestDto): ResponseEntity<SignInResponseDto> {
        val result = signIn.execute(mapper.toCommand(req))

        if (result.mfaRequired) {
            return ResponseEntity.ok(SignInResponseDto(mfaRequired = true))
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
    fun logout(): ResponseEntity<Any> {
        val cookie = mapper.toDeletionCookie()

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .build()
    }


    @PostMapping("/reset-password")
    fun requestPasswordReset(@RequestParam @Email @Valid email: String): ResponseEntity<Any> {
        resetPassword.execute(email)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/reset-password/confirm")
    fun resetPassword(
        @RequestBody req: PasswordResetRequestDto,
    ): ResponseEntity<Any> {
        resetPassword.execute(req.code, req.password)
        return ResponseEntity.ok().build()
    }


    @GetMapping("/validate-code")
    fun validatePasswordResetCode(@RequestParam code: String): ResponseEntity<Any> {
        resetPassword.validateCode(code)
        return ResponseEntity.ok().build()
    }
}