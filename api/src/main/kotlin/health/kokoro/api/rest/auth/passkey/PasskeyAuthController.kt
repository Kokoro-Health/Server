package health.kokoro.api.rest.auth.passkey

import health.kokoro.application.usecase.auth.passkey.AuthPasskeyFinish
import health.kokoro.application.usecase.auth.passkey.AuthPasskeyStart
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth/passkey-authentication")
@Tag(name = "Passkey Authentication", description = "Authenticate with passkeys (WebAuthn)")
class PasskeyAuthController(
    private val authPasskeyStart: AuthPasskeyStart,
    private val authPasskeyFinish: AuthPasskeyFinish,
) {
    @PostMapping("/start")
    @Operation(summary = "Start passkey authentication", description = "Initiates WebAuthn get() ceremony")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Authentication options generated"),
        ApiResponse(responseCode = "400", description = "User not found")
    )
    fun passkeyAuthStart(
        @Valid @RequestBody request: AuthPasskeyStartRequestDto
    ): ResponseEntity<AuthPasskeyStartResponseDto> {
        val options = authPasskeyStart.executeToJson(request.email)
        return ResponseEntity.ok(
            AuthPasskeyStartResponseDto(options = options)
        )
    }

    @PostMapping("/finish")
    @Operation(summary = "Complete passkey authentication", description = "Verify assertion and issue JWT")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Authentication successful"),
        ApiResponse(responseCode = "400", description = "Invalid assertion")
    )
    fun passkeyAuthFinish(
        @Valid @RequestBody request: AuthPasskeyFinishRequestDto,
        req: HttpServletRequest
    ): ResponseEntity<AuthPasskeyFinishResponseDto> {
        val token = authPasskeyFinish.execute(request.email, request.credential, req)
        return ResponseEntity.ok(AuthPasskeyFinishResponseDto(token = token))
    }
}
