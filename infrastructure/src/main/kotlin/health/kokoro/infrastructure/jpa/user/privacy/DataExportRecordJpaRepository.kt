package health.kokoro.infrastructure.jpa.user.privacy

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface DataExportRecordJpaRepository : JpaRepository<DataExportRecordEntity, UUID> {
    fun findByUserId(userId: UUID): List<DataExportRecordEntity>

    @Query("SELECT e FROM DataExportRecordEntity e WHERE e.status = 'PENDING' OR e.status = 'PROCESSING'")
    fun findPendingExports(): List<DataExportRecordEntity>
}
