package health.kokoro.api.rest.auth.passkey

import health.kokoro.application.usecase.auth.passkey.DeletePasskey
import health.kokoro.application.usecase.auth.passkey.ListPasskeys
import health.kokoro.application.usecase.auth.passkey.RegisterPasskeyFinish
import health.kokoro.application.usecase.auth.passkey.RegisterPasskeyStart
import health.kokoro.domain.model.user.User
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/passkeys")
class PasskeyController(
    private val registerPasskeyStart: RegisterPasskeyStart,
    private val registerPasskeyFinish: RegisterPasskeyFinish,

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
