package health.kokoro.application.security

import health.kokoro.application.config.JwtConfig
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.Instant
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtUtil(
    private val config: JwtConfig,
    private val clock: Clock
) {
    fun generateToken(email: String): String {
        return Jwts.builder()
            .subject(email)
            .issuedAt(Date.from(Instant.now(clock)))
            .expiration(Date.from(Instant.now(clock).plusMillis(config.expiration)))
            .signWith(getKey())
            .compact()
    }

    fun extractEmail(token: String): String {
        return Jwts.parser()
            .verifyWith(getKey())
            .build()
            .parseSignedClaims(token)
            .payload
            .subject
    }

    fun getExpiration(token: String): Long {
        return Jwts.parser()
            .verifyWith(getKey())
        .build()
            .parseSignedClaims(token)
            .payload
            .expiration
            .time
    }

    private fun getKey(): SecretKey {
        return Keys.hmacShaKeyFor(config.secret.toByteArray())
    }
}