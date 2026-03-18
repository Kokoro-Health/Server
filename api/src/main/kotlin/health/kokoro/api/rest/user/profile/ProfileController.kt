package health.kokoro.api.rest.user.profile

import health.kokoro.application.usecase.user.GetProfile
import health.kokoro.application.usecase.user.verification.RequestVerificationCode
import health.kokoro.application.usecase.user.verification.VerifyEmailCode
import health.kokoro.domain.model.user.User
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user/profile")
@Validated
class ProfileController(
    private val mapper: ProfileMapper,
    private val getProfile: GetProfile,
    private val requestVerificationCode: RequestVerificationCode,
    private val verifyCode: VerifyEmailCode
) {
    @GetMapping
    fun getMyProfile(): ResponseEntity<ProfileResponseDto> {
        val user = SecurityContextHolder.getContext().authentication.principal as User
        val profile = getProfile.execute(user.id!!)
        return ResponseEntity.ok(
            mapper.toDto(profile)
        )
    }

    @PostMapping("/requestVerification")
    fun requestVerificationCode(): ResponseEntity<VerificationRequestResponseDto> {
        val user = SecurityContextHolder.getContext().authentication.principal as User
        val response = requestVerificationCode.execute(user)
        return ResponseEntity.ok(
            VerificationRequestResponseDto(response.nextAllowedAt)
        )
    }

    @PostMapping("/verify")
    fun verifyCode(@RequestParam code: String): ResponseEntity<Any> {
        val user = SecurityContextHolder.getContext().authentication.principal as User
        verifyCode.execute(user, code)
        return ResponseEntity.ok().build()
    }
}