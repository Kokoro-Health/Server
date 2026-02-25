package health.kokoro.api.rest.auth

import health.kokoro.application.auth.SignIn
import health.kokoro.application.auth.SignUp
import jakarta.validation.Valid
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
    private val signUp: SignUp, private val signIn: SignIn, private val mapper: AuthDtoMapper
) {
    @PostMapping("/signup")
    fun signUp(@RequestBody @Valid req: SignUpRequestDto): ResponseEntity<AuthResponseDto> {
        return ResponseEntity.ok(
            mapper.toResponse(
                signUp.execute(mapper.toCommand(req))
            )
        )
    }

    @PostMapping("/signin")
    fun signIn(@RequestBody @Valid req: SignInRequestDto): ResponseEntity<AuthResponseDto> {
        return ResponseEntity.ok(
            mapper.toResponse(signIn.execute(mapper.toCommand(req)))
        )
    }
}