package health.kokoro.application.security

import health.kokoro.domain.port.UserRepository
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(private val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(email: String): UserDetails? {
        if (!userRepository.existsByEmail(email)) throw IllegalArgumentException("User not found")
        val user = userRepository.findByEmail(email)!!

        return User(
            user.email, user.passwordHash,
            emptyList()
        )
    }

}