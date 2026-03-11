package health.kokoro.api.rest.user.settings

import health.kokoro.application.usecase.user.settings.GetUserSettings
import health.kokoro.application.usecase.user.settings.UpdateUserSettings
import health.kokoro.domain.model.user.User
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user/settings")
@Validated
class SettingsController(
    private val getUserSettings: GetUserSettings,
    private val updateUserSettings: UpdateUserSettings,
    private val mapper: SettingsDtoMapper
) {
    @GetMapping
    fun getSettings(): ResponseEntity<SettingsResponseDto> {
        val user = SecurityContextHolder.getContext().authentication.principal as User
        val settings = getUserSettings.execute(user)
        return ResponseEntity.ok(mapper.toDto(settings))
    }

    @PutMapping
    fun updateSettings(
        @RequestBody @Valid body: SettingsRequestDto
    ): ResponseEntity<SettingsResponseDto> {
        val user = SecurityContextHolder.getContext().authentication.principal as User
        val updated = updateUserSettings.execute(mapper.toDomain(body, user))
        return ResponseEntity.ok(mapper.toDto(updated))
    }
}