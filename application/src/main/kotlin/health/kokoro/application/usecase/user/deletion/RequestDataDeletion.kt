package health.kokoro.application.usecase.user.deletion

import health.kokoro.application.usecase.util.CodeGenerator
import health.kokoro.application.usecase.util.RequestDetails
import health.kokoro.domain.model.audit.AuditAction
import health.kokoro.domain.model.audit.AuditEvent
import health.kokoro.domain.model.data.DataDeletionRequest
import health.kokoro.domain.model.user.User
import health.kokoro.domain.port.audit.AuditEventRepository
import health.kokoro.domain.port.data.DataDeletionRequestRepository
import health.kokoro.domain.port.mail.MailSenderRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDate
import java.util.*

@Service
class RequestDataDeletion(
    private val deletionRequestRepository: DataDeletionRequestRepository,
    private val codeGenerator: CodeGenerator,
    private val mailSender: MailSenderRepository,
    private val auditLog: AuditEventRepository
) {
    fun execute(user: User, req: HttpServletRequest): DataDeletionRequest {
        val existing = deletionRequestRepository.findByUserId(user.id!!)
        if (existing != null && existing.confirmed) {
            throw IllegalStateException("Data deletion already confirmed")
        }

        val code = codeGenerator.generate6Digit()
        val request = deletionRequestRepository.request(user.id!!, code)

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
        addAuditLog(user, req)
        return request
    }

    fun addAuditLog(user: User, request: HttpServletRequest) {
        val details = RequestDetails(request)
        val event = AuditEvent(
            id = UUID.randomUUID(),
            userId = user.id!!,
            action = AuditAction.DATA_DELETION_REQUEST,
            userAgent = details.getUserAgent(),
            ipAddress = details.getIpAddress(),
            metaData = null,
            timeStamp = Instant.now()
        )
        auditLog.add(event)
    }
}