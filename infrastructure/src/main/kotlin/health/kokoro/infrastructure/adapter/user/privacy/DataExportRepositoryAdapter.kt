package health.kokoro.infrastructure.adapter.user.privacy

import health.kokoro.domain.model.user.privacy.DataExportRecord
import health.kokoro.domain.port.user.privacy.DataExportRepository
import health.kokoro.infrastructure.jpa.user.privacy.DataExportRecordJpaRepository
import health.kokoro.infrastructure.jpa.user.privacy.DataExportRecordMapper
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class DataExportRepositoryAdapter(
    private val jpa: DataExportRecordJpaRepository,
    private val mapper: DataExportRecordMapper
) : DataExportRepository {

    override fun save(record: DataExportRecord): DataExportRecord {
        return mapper.toDomain(jpa.save(mapper.toEntity(record)))
    }

    override fun findById(id: UUID): DataExportRecord? {
        return jpa.findById(id).orElse(null)?.let { mapper.toDomain(it) }
    }

    override fun findByUserId(userId: UUID): List<DataExportRecord> {
        return jpa.findByUserId(userId).map { mapper.toDomain(it) }
    }

    override fun findPendingExports(): List<DataExportRecord> {
        return jpa.findPendingExports().map { mapper.toDomain(it) }
    }

    override fun update(record: DataExportRecord) {
        jpa.save(mapper.toEntity(record))
    }
}
