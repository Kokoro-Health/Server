package health.kokoro.api.rest.user.profile

import health.kokoro.application.usecase.user.GetProfile
import health.kokoro.application.usecase.user.UpdateProfile
import health.kokoro.application.usecase.user.UploadProfilePicture
import health.kokoro.application.usecase.user.verification.RequestVerificationCode
import health.kokoro.application.usecase.user.verification.VerifyEmailCode
import health.kokoro.domain.model.user.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/users/profile")
@Validated
@Tag(name = "Profile", description = "User profile management")
class ProfileController(
    private val mapper: ProfileMapper,
    private val getProfile: GetProfile,
    private val requestVerificationCode: RequestVerificationCode,
    private val verifyCode: VerifyEmailCode,
    private val uploadProfilePicture: UploadProfilePicture,
    private val updateProfile: UpdateProfile
) {
    @GetMapping
    @Operation(summary = "Get current user profile")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Profile retrieved"),
        ApiResponse(responseCode = "401", description = "Unauthorized")
    )
    fun getMyProfile(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ProfileResponseDto> {
        val profile = getProfile.execute(user.id!!)
        return ResponseEntity.ok(mapper.toDto(profile))
    }

    @PutMapping
    @Operation(summary = "Update user profile")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Profile updated"),
        ApiResponse(responseCode = "400", description = "Validation failed"),
        ApiResponse(responseCode = "401", description = "Unauthorized")
    )
    fun updateProfile(
        @Valid @RequestBody profile: ProfileRequestDto,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<Unit> {
        updateProfile.execute(mapper.toCommand(profile), user)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/verify/request")
    @Operation(summary = "Request new verification code", description = "Rate-limited to one request per minute")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Verification code sent"),
        ApiResponse(responseCode = "401", description = "Unauthorized")
    )
    fun requestVerificationCode(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<VerificationRequestResponseDto> {
        val response = requestVerificationCode.execute(user)
        return ResponseEntity.ok(
            VerificationRequestResponseDto(response.nextAllowedAt)
        )
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify email with code")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Email verified"),
        ApiResponse(responseCode = "400", description = "Invalid or expired code")
    )
    fun verifyCode(
        @Parameter(description = "Verification code", example = "123456")
        @RequestParam code: String,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<Unit> {
        verifyCode.execute(user, code)
        return ResponseEntity.ok().build()
    }

    @PostMapping(path = ["/profilePicture"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @Operation(summary = "Upload profile picture", description = "Accepts image files.")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Picture uploaded"),
        ApiResponse(responseCode = "400", description = "Invalid file type or size"),
        ApiResponse(responseCode = "401", description = "Unauthorized")
    )
    fun uploadProfilePicture(
        @RequestPart @Schema(description = "Profile picture file") file: MultipartFile,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<Unit> {
        uploadProfilePicture.execute(file, user)
        return ResponseEntity.ok().build()
    }
}
