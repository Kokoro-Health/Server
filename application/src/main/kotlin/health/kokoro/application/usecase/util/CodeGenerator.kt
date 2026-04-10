package health.kokoro.application.usecase.util

import org.springframework.stereotype.Service
import java.security.SecureRandom

@Service
class CodeGenerator(private val secureRandom: SecureRandom) {
    fun generate6Digit(): String {
        val number = secureRandom.nextInt(1_000_000)
        return "%06d".format(number)
    }
}