package health.kokoro.application.bean

import health.kokoro.application.security.JwtFilter
import health.kokoro.application.security.UserDetailsServiceImpl
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
    private val userDetailsService: UserDetailsServiceImpl
) {
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { it.disable() }
            .httpBasic { it.disable() }
            .cors { cors ->
                cors.configurationSource { request ->
                    val corsConfig = CorsConfiguration()
                    corsConfig.allowedOrigins = listOf(
                        "http://192.168.1.162:5173",
                        "capacitor://localhost",
                        "http://localhost:5173"
                    )
                    corsConfig.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    corsConfig.allowedHeaders = listOf("*")
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
}