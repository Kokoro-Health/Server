package health.kokoro.domain.port.data

import health.kokoro.domain.model.data.DataDeletionRequest
import java.time.Instant
import java.util.UUID

interface DataDeletionRequestRepository {
    fun request(userId: UUID, confirmationCode: String): DataDeletionRequest
    fun confirm(userId: UUID, code: String): Boolean
    fun abort(userId: UUID)
    fun findByUserId(userId: UUID): DataDeletionRequest?
    fun findAll(): List<DataDeletionRequest>
    fun findDeletableRequests(deletionDate: Instant): List<DataDeletionRequest>
    fun deleteByUserId(userId: UUID)
}