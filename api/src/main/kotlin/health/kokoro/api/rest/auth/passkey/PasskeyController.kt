package health.kokoro.api.rest.auth.passkey

import health.kokoro.application.usecase.auth.passkey.AuthPasskeyFinish
import health.kokoro.application.usecase.auth.passkey.AuthPasskeyStart
import health.kokoro.application.usecase.auth.passkey.DeletePasskey
import health.kokoro.application.usecase.auth.passkey.ListPasskeys
import health.kokoro.application.usecase.auth.passkey.RegisterPasskeyFinish
import health.kokoro.application.usecase.auth.passkey.RegisterPasskeyStart
import health.kokoro.domain.model.user.User
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

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
    ): ResponseEntity<RegisterPasskeyStartResponse> {
        val options = registerPasskeyStart.executeToJson(user)
        return ResponseEntity.ok(
            RegisterPasskeyStartResponse(options = options)
        )
    }

    @PostMapping("/register/finish")
    fun passkeyRegisterFinish(
        @AuthenticationPrincipal user: User,
        @RequestBody request: RegisterPasskeyFinishRequest
    ): ResponseEntity<RegisterPasskeyFinishResponse> {
        val passkey = registerPasskeyFinish.execute(user, request.credential, request.deviceName)
        return ResponseEntity.ok(passkeyDtoMapper.toRegisterFinishResponse(passkey))
    }

    @PostMapping("/auth/start")
    fun passkeyAuthStart(
        @RequestBody request: AuthPasskeyStartRequest
    ): ResponseEntity<AuthPasskeyStartResponse> {
        val options = authPasskeyStart.executeToJson(request.email)
        return ResponseEntity.ok(
            AuthPasskeyStartResponse(options = options)
        )
    }

    @PostMapping("/auth/finish")
    fun passkeyAuthFinish(
        @RequestBody request: AuthPasskeyFinishRequest
    ): ResponseEntity<AuthPasskeyFinishResponse> {
        val token = authPasskeyFinish.execute(request.email, request.credential)
        return ResponseEntity.ok(AuthPasskeyFinishResponse(token = token))
    }

    @GetMapping
    fun passkeyList(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<List<PasskeyResponse>> {
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
