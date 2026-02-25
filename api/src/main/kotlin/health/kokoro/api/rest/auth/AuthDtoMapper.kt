package health.kokoro.api.rest.auth

import health.kokoro.application.usecase.auth.AuthResponse
import health.kokoro.application.usecase.auth.SignIn
import health.kokoro.application.usecase.auth.SignUp
import org.springframework.stereotype.Component

@Component
class AuthDtoMapper {
    fun toResponse(dto: AuthResponse): AuthResponseDto {
        return AuthResponseDto(dto.token, dto.expiresIn)
    }

    fun toCommand(dto: SignUpRequestDto): SignUp.Command {
        return SignUp.Command(dto.firstName, dto.middleName, dto.lastName, dto.email, dto.password)
    }

    fun toCommand(dto: SignInRequestDto): SignIn.Command {
        return SignIn.Command(dto.email, dto.password)
    }
}