package health.kokoro.api.rest.auth.passkey

import health.kokoro.application.usecase.auth.passkey.*
import health.kokoro.domain.model.user.User
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/auth/passkeys")
class PasskeyController(
    private val registerPasskeyStart: RegisterPasskeyStart,
    private val registerPasskeyFinish: RegisterPasskeyFinish,
    private val authPasskeyStart: AuthPasskeyStart,
    private val authPasskeyFinish: AuthPasskeyFinish,
    private val deletePasskey: DeletePasskey,
    private val listPasskeys: ListPasskeys,
    private val passkeyDtoMapper: PasskeyDtoMapper,
) {

    @PostMapping("/register/start")
    fun passkeyRegisterStart(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<RegisterPasskeyStartResponseDto> {
        val options = registerPasskeyStart.executeToJson(user)
        return ResponseEntity.ok(
            RegisterPasskeyStartResponseDto(options = options)
        )
    }

    @PostMapping("/register/finish")
    fun passkeyRegisterFinish(
        @AuthenticationPrincipal user: User,
        @RequestBody request: RegisterPasskeyFinishRequestDto
    ): ResponseEntity<RegisterPasskeyFinishResponseDto> {
        val passkey = registerPasskeyFinish.execute(user, request.credential, request.deviceName)
        return ResponseEntity.ok(passkeyDtoMapper.toRegisterFinishResponse(passkey))
    }

    @PostMapping("/auth/start")
    fun passkeyAuthStart(
        @RequestBody request: AuthPasskeyStartRequestDto
    ): ResponseEntity<AuthPasskeyStartResponseDto> {
        val options = authPasskeyStart.executeToJson(request.email)
        return ResponseEntity.ok(
            AuthPasskeyStartResponseDto(options = options)
        )
    }

    @PostMapping("/auth/finish")
    fun passkeyAuthFinish(
        @RequestBody request: AuthPasskeyFinishRequestDto
    ): ResponseEntity<AuthPasskeyFinishResponseDto> {
        val token = authPasskeyFinish.execute(request.email, request.credential)
        return ResponseEntity.ok(AuthPasskeyFinishResponseDto(token = token))
    }

    @GetMapping
    fun passkeyList(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<List<PasskeyResponseDto>> {
        val passkeys = listPasskeys.execute(user.id!!)
        return ResponseEntity.ok(passkeys.map { passkeyDtoMapper.toPasskeyResponse(it) })
    }

    @DeleteMapping("/{passkeyId}")
    fun passkeyDelete(
        @AuthenticationPrincipal user: User,
        @PathVariable passkeyId: UUID
    ): ResponseEntity<Unit> {
        deletePasskey.execute(passkeyId, user.id!!)
        return ResponseEntity.noContent().build()
    }
}
