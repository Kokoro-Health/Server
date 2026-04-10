package health.kokoro.infrastructure.jpa.data

import health.kokoro.domain.model.data.DataDeletionRequest
import health.kokoro.infrastructure.jpa.user.UserJpaRepository
import org.springframework.stereotype.Component

@Component
class DataDeletionRequestMapper(
    private val userJpaRepository: UserJpaRepository,
) {
    fun toEntity(domain: DataDeletionRequest): DataDeletionRequestEntity {
        val user = userJpaRepository.findById(domain.userId).orElseThrow()
        return DataDeletionRequestEntity(
            user = user,
            confirmedAt = domain.confirmedAt,
            confirmationCode = domain.confirmationCode,
            codeRequestedAt = domain.codeRequestedAt
        )
    }

    fun toDomain(entity: DataDeletionRequestEntity): DataDeletionRequest {
        return DataDeletionRequest(
            confirmationCode = entity.confirmationCode,
            confirmedAt = entity.confirmedAt,
            confirmed = entity.confirmedAt != null,
            createdAt = entity.createdAt,
            id = entity.id!!,
            userId = entity.user.id!!,
            codeRequestedAt = entity.codeRequestedAt
        )
    }
}