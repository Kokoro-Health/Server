package health.kokoro.api.rest.auth

import health.kokoro.application.usecase.auth.AuthResponse
import health.kokoro.application.usecase.auth.SignIn
import health.kokoro.application.usecase.auth.SignUp
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Component

@Component
class AuthDtoMapper {
    fun toCookie(response: AuthResponse, rememberMe: Boolean): ResponseCookie {
        val age = if (rememberMe) response.expiresIn / 1000 else -1L
        return ResponseCookie.from("access_token", response.token)
            .path("/")
            .httpOnly(true)
            .secure(true)
            .maxAge(age)
            .build()
    }

    fun toCommand(dto: SignUpRequestDto): SignUp.Command {
        return SignUp.Command(dto.firstName, dto.middleName, dto.lastName, dto.email, dto.password)
    }

    fun toCommand(dto: SignInRequestDto): SignIn.Command {
        return SignIn.Command(dto.email, dto.password)
    }
}