package health.kokoro.application.security

import health.kokoro.domain.port.user.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtFilter(private val jwtUtil: JwtUtil, private val userRepo: UserRepository) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7).trim()
            try {
                val email = jwtUtil.extractEmail(token) ?: throw IllegalArgumentException()
                val user = userRepo.findByEmail(email)
                if (user != null) {
                    val auth = UsernamePasswordAuthenticationToken(user, null, emptyList())
                    SecurityContextHolder.getContext().authentication = auth
                }
            } catch (_: Exception) {
                logger.info("Invalid JWT token: $token")
            }
        }

        filterChain.doFilter(request, response)
    }
}