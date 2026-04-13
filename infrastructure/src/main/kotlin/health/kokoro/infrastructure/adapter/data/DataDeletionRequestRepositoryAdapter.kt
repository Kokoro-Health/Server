package health.kokoro.infrastructure.adapter.data

import health.kokoro.domain.model.data.DataDeletionRequest
import health.kokoro.domain.port.data.DataDeletionRequestRepository
import health.kokoro.infrastructure.jpa.data.DataDeletionRequestEntity
import health.kokoro.infrastructure.jpa.data.DataDeletionRequestJpaRepository
import health.kokoro.infrastructure.jpa.data.DataDeletionRequestMapper
import health.kokoro.infrastructure.jpa.user.UserJpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*

@Repository
class DataDeletionRequestRepositoryAdapter(
    private val jpa: DataDeletionRequestJpaRepository,
    private val mapper: DataDeletionRequestMapper,
    private val userJpaRepository: UserJpaRepository
) : DataDeletionRequestRepository {
    override fun request(userId: UUID, confirmationCode: String): DataDeletionRequest {
        val user = userJpaRepository.findById(userId).orElseThrow { IllegalArgumentException("User not found") }
        val now = Instant.now()

        val entity = jpa.findByUserId(userId)?.copy(
            confirmationCode = confirmationCode,
            codeRequestedAt = now,
            confirmedAt = null
        ) ?: DataDeletionRequestEntity(
            user = user,
            confirmedAt = null,
            confirmationCode = confirmationCode,
            codeRequestedAt = now
        )

        return mapper.toDomain(jpa.save(entity))
    }

    override fun confirm(userId: UUID, code: String): Boolean {
        val entity = jpa.findByUserId(userId) ?: return false
        if (entity.confirmationCode != code) return false
        entity.confirmedAt = Instant.now()
        entity.updatedAt = Instant.now()
        jpa.saveAndFlush(entity)
        return true
    }

    override fun abort(userId: UUID) {
        val entity = jpa.findByUserId(userId) ?: return
        jpa.delete(entity)
    }

    override fun findByUserId(userId: UUID): DataDeletionRequest? {
        return jpa.findByUserId(userId)?.let { mapper.toDomain(it) }
    }

    override fun findAll(): List<DataDeletionRequest> {
        return jpa.findAll().map { mapper.toDomain(it) }
    }

    override fun findDeletableRequests(deletionDate: Instant): List<DataDeletionRequest> {
        return jpa.findByConfirmedAtBefore(deletionDate).map { mapper.toDomain(it) }
    }

    override fun deleteByUserId(userId: UUID) {
        jpa.deleteByUserId(userId)
    }
}