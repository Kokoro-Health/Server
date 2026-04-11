package health.kokoro.domain.port.user.privacy

import health.kokoro.domain.model.user.privacy.DataExportRecord
import java.util.*

interface DataExportRepository {
    fun save(record: DataExportRecord): DataExportRecord
    fun findById(id: UUID): DataExportRecord?
    fun findByUserId(userId: UUID): List<DataExportRecord>
    fun findPendingExports(): List<DataExportRecord>
    fun update(record: DataExportRecord)
}
