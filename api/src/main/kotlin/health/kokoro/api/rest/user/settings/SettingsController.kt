package health.kokoro.api.rest.user.settings

import health.kokoro.application.usecase.user.settings.GetUserSettings
import health.kokoro.application.usecase.user.settings.UpdateUserSettings
import health.kokoro.domain.model.user.User
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users/settings")
@Validated
class SettingsController(
    private val getUserSettings: GetUserSettings,
    private val updateUserSettings: UpdateUserSettings,
    private val mapper: SettingsDtoMapper
) {
    @GetMapping
    fun getSettings(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<SettingsResponseDto> {
        val settings = getUserSettings.execute(user)
        return ResponseEntity.ok(mapper.toDto(settings))
    }

    @PutMapping
    fun updateSettings(
        @AuthenticationPrincipal user: User,
        @RequestBody @Valid body: SettingsRequestDto
    ): ResponseEntity<Unit> {
        updateUserSettings.execute(user, mapper.toDomain(body))
        return ResponseEntity.noContent().build()
    }
}