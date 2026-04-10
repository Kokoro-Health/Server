package health.kokoro.application.usecase.user.deletion

import health.kokoro.application.usecase.util.CodeGenerator
import health.kokoro.domain.model.data.DataDeletionRequest
import health.kokoro.domain.port.data.DataDeletionRequestRepository
import health.kokoro.domain.port.mail.MailSenderRepository
import health.kokoro.domain.port.user.UserRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.UUID

@Service
class RequestDataDeletion(
    private val userRepository: UserRepository,
    private val deletionRequestRepository: DataDeletionRequestRepository,
    private val codeGenerator: CodeGenerator,
    private val mailSender: MailSenderRepository
) {
    fun execute(userId: UUID): DataDeletionRequest {
        val user = userRepository.findById(userId) ?: throw IllegalArgumentException("User not found")

        val existing = deletionRequestRepository.findByUserId(userId)
        if (existing != null && existing.confirmed) {
            throw IllegalStateException("Data deletion already confirmed")
        }

        val code = codeGenerator.generate6Digit()
        val request = deletionRequestRepository.request(userId, code)

        mailSender.sendTemplate(
            to = user.email,
            template = "data-deletion-request",
            subject = "Confirm data deletion",
            model = mapOf(
                "code" to code,
                "expirationDays" to 7,
                "year" to LocalDate.now().year,
                "email" to user.email
            )
        )

        return request
    }
}