package health.kokoro.api.rest.auth


import health.kokoro.application.usecase.auth.AuthResponse
import health.kokoro.application.usecase.auth.SignIn
import health.kokoro.application.usecase.auth.SignUp
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
@Validated
class AuthController(
    private val signUp: SignUp,
    private val signIn: SignIn,
    private val mapper: AuthDtoMapper
) {
    @PostMapping("/signup")
    fun signUp(@RequestBody @Valid req: SignUpRequestDto): ResponseEntity<Unit> {
        val result = signUp.execute(mapper.toCommand(req))
        val cookie = mapper.toCookie(result, false)

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .build()
    }

    @PostMapping("/signin")
    fun signIn(@RequestBody @Valid req: SignInRequestDto): ResponseEntity<Unit> {
        val result = signIn.execute(mapper.toCommand(req))
        val cookie = mapper.toCookie(result, req.rememberMe)

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .build()
    }

    @PostMapping("/logout")
    fun logout(): ResponseEntity<Any> {
        val cookie = mapper.toDeletionCookie()

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .build()
    }
}