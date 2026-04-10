package health.kokoro.application.usecase.user.deletion

import health.kokoro.application.usecase.util.RequestDetails
import health.kokoro.domain.error.InvalidVerificationCodeException
import health.kokoro.domain.model.audit.AuditAction
import health.kokoro.domain.model.audit.AuditEvent
import health.kokoro.domain.model.user.User
import health.kokoro.domain.port.audit.AuditEventRepository
import health.kokoro.domain.port.data.DataDeletionRequestRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class ConfirmDataDeletion(
    private val deletionRequestRepository: DataDeletionRequestRepository,
    private val auditLog: AuditEventRepository
) {
    fun execute(user: User, code: String, req: HttpServletRequest) {
        val request = deletionRequestRepository.findByUserId(user.id!!)
            ?: throw InvalidVerificationCodeException()

        if (request.confirmationCode != code) {
            throw InvalidVerificationCodeException()
        }

        if (request.confirmed) {
            return
        }
        addAuditLog(user, req)
        deletionRequestRepository.confirm(user.id!!, code)
    }

    fun addAuditLog(user: User, request: HttpServletRequest) {
        val details = RequestDetails(request)
        val event = AuditEvent(
            id = UUID.randomUUID(),
            userId = user.id!!,
            action = AuditAction.DATA_DELETION_CONFIRMED,
            userAgent = details.getUserAgent(),
            ipAddress = details.getIpAddress(),
            metaData = null,
            timeStamp = Instant.now()
        )
        auditLog.add(event)
    }
}