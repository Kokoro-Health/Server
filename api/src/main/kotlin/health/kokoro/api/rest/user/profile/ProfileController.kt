package health.kokoro.api.rest.user.profile

import health.kokoro.application.usecase.user.GetProfile
import health.kokoro.application.usecase.user.UpdateProfile
import health.kokoro.application.usecase.user.UploadProfilePicture
import health.kokoro.application.usecase.user.verification.RequestVerificationCode
import health.kokoro.application.usecase.user.verification.VerifyEmailCode
import health.kokoro.domain.model.user.User
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
class ProfileController(
    private val mapper: ProfileMapper,
    private val getProfile: GetProfile,
    private val requestVerificationCode: RequestVerificationCode,
    private val verifyCode: VerifyEmailCode,
    private val uploadProfilePicture: UploadProfilePicture,
    private val updateProfile: UpdateProfile
) {
    @GetMapping
    fun getMyProfile(@AuthenticationPrincipal user: User): ResponseEntity<ProfileResponseDto> {
        val profile = getProfile.execute(user.id!!)
        return ResponseEntity.ok(
            mapper.toDto(profile)
        )
    }

    @PostMapping
    fun updateProfile(
        @Valid @RequestBody profile: ProfileRequestDto,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<Unit> {
        updateProfile.execute(mapper.toCommand(profile), user)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/verify/request")
    fun requestVerificationCode(@AuthenticationPrincipal user: User): ResponseEntity<VerificationRequestResponseDto> {
        val response = requestVerificationCode.execute(user)
        return ResponseEntity.ok(
            VerificationRequestResponseDto(response.nextAllowedAt)
        )
    }

    @PostMapping(path = ["/profilePicture"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadProfilePicture(
        @RequestBody file: MultipartFile,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<Unit> {
        uploadProfilePicture.execute(file, user)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/verify")
    fun verifyCode(@RequestParam code: String, @AuthenticationPrincipal user: User): ResponseEntity<Any> {
        verifyCode.execute(user, code)
        return ResponseEntity.ok().build()
    }
}