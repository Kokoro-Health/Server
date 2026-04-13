package health.kokoro.api.rest.auth.mfa

import health.kokoro.application.usecase.auth.GetMfaEnabled
import health.kokoro.application.usecase.auth.totp.DisableMfaTotp
import health.kokoro.application.usecase.auth.totp.SetupMfaTotp
import health.kokoro.application.usecase.auth.totp.VerifyMfaTotp
import health.kokoro.domain.model.user.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth/mfa")
@Validated
@Tag(name = "MFA", description = "Multi-factor authentication (TOTP)")
class MfaController(
    private val setupMfaTotp: SetupMfaTotp,
    private val verifyMfaTotp: VerifyMfaTotp,
    private val disableMfaTotp: DisableMfaTotp,
    private val getMfaEnabled: GetMfaEnabled,
    private val mfaMapper: MfaMapper
) {
    @GetMapping
    @Operation(summary = "Get MFA status")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "MFA status retrieved"),
        ApiResponse(responseCode = "401", description = "Unauthorized")
    )
    fun getMfaSettings(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<MfaSettingsResponseDto> {
        return ResponseEntity.ok(
            mfaMapper.toResponse(getMfaEnabled.execute(user))
        )
    }

    @PostMapping("/setup")
    @Operation(summary = "Setup MFA", description = "Returns TOTP secret and QR code. Save the secret securely.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "MFA setup initiated"),
        ApiResponse(responseCode = "401", description = "Unauthorized"),
        ApiResponse(responseCode = "409", description = "MFA already enabled")
    )
    fun setupMfa(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<SetupMfaResponseDto> {
        val response = setupMfaTotp.execute(user)
        return ResponseEntity.ok(SetupMfaResponseDto(response.secret, response.qrCodeBase64))
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify MFA code and enable", description = "Enter 6-digit code from authenticator app")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "MFA enabled"),
        ApiResponse(responseCode = "400", description = "Invalid code"),
        ApiResponse(responseCode = "401", description = "Unauthorized")
    )
    fun verifyMfaCodeAndEnable(
        @Valid @RequestBody request: VerifyMfaRequestDto,
        @AuthenticationPrincipal user: User,
        req: HttpServletRequest
    ): ResponseEntity<Unit> {
        verifyMfaTotp.executeAndEnable(user, request.code, req)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping
    @Operation(summary = "Disable MFA", description = "Requires password verification")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "MFA disabled"),
        ApiResponse(responseCode = "400", description = "Wrong password"),
        ApiResponse(responseCode = "401", description = "Unauthorized")
    )
    fun disableMfa(
        @Valid @RequestBody request: DisableMfaRequestDto,
        @AuthenticationPrincipal user: User,
        req: HttpServletRequest
    ): ResponseEntity<Unit> {
        disableMfaTotp.execute(user, request.password, req)
        return ResponseEntity.noContent().build()
    }
}
