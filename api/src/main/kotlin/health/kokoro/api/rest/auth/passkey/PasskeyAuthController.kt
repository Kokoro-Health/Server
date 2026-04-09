package health.kokoro.api.rest.auth.passkey

import health.kokoro.application.usecase.auth.passkey.AuthPasskeyFinish
import health.kokoro.application.usecase.auth.passkey.AuthPasskeyStart
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth/passkey-authentication")
class PasskeyAuthController(
    private val authPasskeyStart: AuthPasskeyStart,
    private val authPasskeyFinish: AuthPasskeyFinish,
) {
    @PostMapping("/start")
    fun passkeyAuthStart(
        @RequestBody request: AuthPasskeyStartRequestDto
    ): ResponseEntity<AuthPasskeyStartResponseDto> {
        val options = authPasskeyStart.executeToJson(request.email)
        return ResponseEntity.ok(
            AuthPasskeyStartResponseDto(options = options)
        )
    }

    @PostMapping("/finish")
    fun passkeyAuthFinish(
        @RequestBody request: AuthPasskeyFinishRequestDto
    ): ResponseEntity<AuthPasskeyFinishResponseDto> {
        val token = authPasskeyFinish.execute(request.email, request.credential)
        return ResponseEntity.ok(AuthPasskeyFinishResponseDto(token = token))
    }
}