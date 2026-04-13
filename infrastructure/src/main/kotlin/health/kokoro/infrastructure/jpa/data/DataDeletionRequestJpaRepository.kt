package health.kokoro.infrastructure.jpa.data

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*

@Repository
interface DataDeletionRequestJpaRepository : JpaRepository<DataDeletionRequestEntity, UUID> {
    fun findByUserId(id: UUID): DataDeletionRequestEntity?
    fun findByConfirmedAtBefore(before: Instant): List<DataDeletionRequestEntity>
}