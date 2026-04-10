package health.kokoro.api.rest.auth.mfa

import health.kokoro.application.usecase.auth.GetMfaEnabled
import health.kokoro.application.usecase.auth.totp.DisableMfaTotp
import health.kokoro.application.usecase.auth.totp.SetupMfaTotp
import health.kokoro.application.usecase.auth.totp.VerifyMfaTotp
import health.kokoro.domain.model.user.User
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth/mfa")
@Validated
class MfaController(
    private val setupMfaTotp: SetupMfaTotp,
    private val verifyMfaTotp: VerifyMfaTotp,
    private val disableMfaTotp: DisableMfaTotp,
    private val getMfaEnabled: GetMfaEnabled,
    private val mfaMapper: MfaMapper
) {
    @GetMapping
    fun getMfaSettings(@AuthenticationPrincipal user: User): ResponseEntity<MfaSettingsResponseDto> {
        return ResponseEntity.ok(
            mfaMapper.toResponse(getMfaEnabled.execute(user))
        )
    }

    @PostMapping("/setup")
    fun setupMfa(@AuthenticationPrincipal user: User): ResponseEntity<SetupMfaResponseDto> {
        val response = setupMfaTotp.execute(user)
        return ResponseEntity.ok(SetupMfaResponseDto(response.secret, response.qrCodeBase64))
    }

    @PostMapping("/verify")
    fun verifyMfaCodeAndEnable(
        @RequestBody @Valid request: VerifyMfaRequestDto,
        @AuthenticationPrincipal user: User,
        req: HttpServletRequest
    ): ResponseEntity<Unit> {
        verifyMfaTotp.executeAndEnable(user, request.code, req)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping
    fun disableMfa(
        @RequestBody @Valid request: DisableMfaRequestDto,
        @AuthenticationPrincipal user: User,
        req: HttpServletRequest
    ): ResponseEntity<Unit> {
        disableMfaTotp.execute(user, request.password, req)
        return ResponseEntity.noContent().build()
    }
}
