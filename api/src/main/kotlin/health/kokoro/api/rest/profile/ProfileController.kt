package health.kokoro.api.rest.profile

import health.kokoro.application.usecase.profile.GetProfile
import health.kokoro.domain.model.User
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/profile")
@Validated
class ProfileController(
    private val mapper: ProfileMapper,
    private val getProfile: GetProfile
) {
    @GetMapping
    fun getMyProfile(): ResponseEntity<ProfileResponseDto> {
        val user = SecurityContextHolder.getContext().authentication.principal as User
        val profile = getProfile.execute(user.id!!)
        return ResponseEntity.ok(
            mapper.toDto(profile)
        )
    }
}