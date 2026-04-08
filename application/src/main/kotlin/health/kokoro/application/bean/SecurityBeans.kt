package health.kokoro.application.bean

import com.yubico.webauthn.CredentialRepository
import com.yubico.webauthn.RelyingParty
import com.yubico.webauthn.data.RelyingPartyIdentity
import health.kokoro.application.config.CorsConfig
import health.kokoro.application.security.JwtFilter
import health.kokoro.application.security.UserDetailsServiceImpl
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration

@Configuration
@EnableWebSecurity
class SecurityBeans(
    private val jwtFilter: JwtFilter,
    private val userDetailsService: UserDetailsServiceImpl,
    private val config: CorsConfig,
) {
    companion object {
        val logger: Logger = LoggerFactory.getLogger(this::class.qualifiedName)
    }
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { it.disable() }
            .httpBasic { it.disable() }
            .cors { cors ->
                cors.configurationSource { request ->
                    logger.info("Received Request: Address ${request.remoteAddr}, Host ${request.remoteHost}, Port ${request.remotePort}, User ${request.remoteUser}")
                    val corsConfig = CorsConfiguration()
                    corsConfig.allowedOrigins = config.allowOrigin
                    corsConfig.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    corsConfig.allowedHeaders = listOf("*", "Referer", "Origin")
                    corsConfig.allowCredentials = true
                    corsConfig.maxAge = 3600L

                    corsConfig
                }
            }
            .authorizeHttpRequests {
                it.requestMatchers("/auth/signin", "/auth/signup", "/auth/reset-password*/**", "/docs*/**").permitAll()
                    .anyRequest().authenticated()
            }
            .userDetailsService(userDetailsService)
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }


        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun authenticationManager(http: HttpSecurity): AuthenticationManager {
        val builder = http.getSharedObject(AuthenticationManagerBuilder::class.java)
        builder.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder())
        return builder.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun relyingParty(credentialRepository: CredentialRepository): RelyingParty {
        return RelyingParty.builder()
            .identity(
                RelyingPartyIdentity.builder()
                    .id("kokoro.health")
                    .name("Kokoro")
                    .build()
            )
            .credentialRepository(credentialRepository)
            .origins(setOf("https://kokoro.health"))
            .build()
    }
}