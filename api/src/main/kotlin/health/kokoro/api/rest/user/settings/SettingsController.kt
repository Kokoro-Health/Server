package health.kokoro.api.rest.user.settings

import health.kokoro.application.usecase.user.settings.GetUserSettings
import health.kokoro.application.usecase.user.settings.UpdateUserSettings
import health.kokoro.domain.model.user.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users/settings")
@Validated
@Tag(name = "Settings", description = "User preferences and settings")
class SettingsController(
    private val getUserSettings: GetUserSettings,
    private val updateUserSettings: UpdateUserSettings,
    private val mapper: SettingsDtoMapper
) {
    @GetMapping
    @Operation(summary = "Get user settings")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Settings retrieved"),
        ApiResponse(responseCode = "401", description = "Unauthorized")
    )
    fun getSettings(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<SettingsResponseDto> {
        val settings = getUserSettings.execute(user)
        return ResponseEntity.ok(mapper.toDto(settings))
    }

    @PutMapping
    @Operation(summary = "Update user settings")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Settings updated"),
        ApiResponse(responseCode = "400", description = "Validation failed"),
        ApiResponse(responseCode = "401", description = "Unauthorized")
    )
    fun updateSettings(
        @AuthenticationPrincipal user: User,
        @Valid @RequestBody body: SettingsRequestDto
    ): ResponseEntity<Unit> {
        updateUserSettings.execute(user, mapper.toDomain(body))
        return ResponseEntity.noContent().build()
    }
}
