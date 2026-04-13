package health.kokoro.api.rest.auth.passkey

import health.kokoro.application.usecase.auth.passkey.DeletePasskey
import health.kokoro.application.usecase.auth.passkey.ListPasskeys
import health.kokoro.application.usecase.auth.passkey.RegisterPasskeyFinish
import health.kokoro.application.usecase.auth.passkey.RegisterPasskeyStart
import health.kokoro.domain.model.user.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/passkeys")
@Tag(name = "Passkeys", description = "Passkey (WebAuthn) management")
class PasskeyController(
    private val registerPasskeyStart: RegisterPasskeyStart,
    private val registerPasskeyFinish: RegisterPasskeyFinish,
    private val deletePasskey: DeletePasskey,
    private val listPasskeys: ListPasskeys,
    private val passkeyDtoMapper: PasskeyDtoMapper,
) {
    @PostMapping("/register/start")
    @Operation(summary = "Start passkey registration", description = "Returns WebAuthn creation options")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Options generated"),
        ApiResponse(responseCode = "401", description = "Unauthorized")
    )
    fun passkeyRegisterStart(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<RegisterPasskeyStartResponseDto> {
        val options = registerPasskeyStart.executeToJson(user)
        return ResponseEntity.ok(
            RegisterPasskeyStartResponseDto(options = options)
        )
    }

    @PostMapping("/register/finish")
    @Operation(summary = "Complete passkey registration", description = "Verify attestation and save credential")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Passkey registered"),
        ApiResponse(responseCode = "400", description = "Invalid attestation"),
        ApiResponse(responseCode = "401", description = "Unauthorized")
    )
    fun passkeyRegisterFinish(
        @AuthenticationPrincipal user: User,
        @Valid @RequestBody request: RegisterPasskeyFinishRequestDto
    ): ResponseEntity<RegisterPasskeyFinishResponseDto> {
        val passkey = registerPasskeyFinish.execute(user, request.credential, request.deviceName)
        return ResponseEntity.ok(passkeyDtoMapper.toRegisterFinishResponse(passkey))
    }

    @GetMapping
    @Operation(summary = "List user's passkeys")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Passkeys retrieved"),
        ApiResponse(responseCode = "401", description = "Unauthorized")
    )
    fun passkeyList(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<List<PasskeyResponseDto>> {
        val passkeys = listPasskeys.execute(user.id!!)
        return ResponseEntity.ok(passkeys.map { passkeyDtoMapper.toPasskeyResponse(it) })
    }

    @DeleteMapping("/{passkeyId}")
    @Operation(summary = "Delete a passkey")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Passkey deleted"),
        ApiResponse(responseCode = "404", description = "Passkey not found"),
        ApiResponse(responseCode = "403", description = "Not your passkey")
    )
    fun passkeyDelete(
        @AuthenticationPrincipal user: User,
        @Parameter(description = "Passkey ID") @PathVariable passkeyId: UUID
    ): ResponseEntity<Unit> {
        deletePasskey.execute(passkeyId, user.id!!)
        return ResponseEntity.noContent().build()
    }
}
