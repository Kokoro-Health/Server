package health.kokoro.application.security

import health.kokoro.domain.port.user.UserRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(private val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(email: String): UserDetails? {
        if (!userRepository.existsByEmail(email)) throw UsernameNotFoundException("User not found")
        val user = userRepository.findByEmail(email)!!
        if (!user.enabled) throw UsernameNotFoundException("User Disabled")

        val authorities = listOf(SimpleGrantedAuthority("ROLE_${user.role.name}"))

        return User(
            user.email, user.security.passwordHash,
            user.enabled, true, true, true,
            authorities
        )
    }

}