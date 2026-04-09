package health.kokoro.api.rest.auth.mfa

import org.springframework.stereotype.Component

@Component
class MfaMapper {
    fun toResponse(enabled: Boolean): MfaSettingsResponseDto {
        return MfaSettingsResponseDto(enabled)
    }
}