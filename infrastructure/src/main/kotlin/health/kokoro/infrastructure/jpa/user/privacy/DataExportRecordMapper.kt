package health.kokoro.infrastructure.jpa.user.privacy

import health.kokoro.domain.model.user.privacy.DataExportRecord
import org.springframework.stereotype.Component

@Component
class DataExportRecordMapper {
    fun toDomain(entity: DataExportRecordEntity): DataExportRecord {
        return DataExportRecord(
            id = entity.id,
            userId = entity.userId,
            requestedAt = entity.requestedAt,
            completedAt = entity.completedAt,
            status = entity.status,
            ipAddress = entity.ipAddress,
            userAgent = entity.userAgent
        )
    }

    fun toEntity(domain: DataExportRecord): DataExportRecordEntity {
        return DataExportRecordEntity(
            id = domain.id!!,
            userId = domain.userId,
            requestedAt = domain.requestedAt,
            completedAt = domain.completedAt,
            status = domain.status,
            ipAddress = domain.ipAddress,
            userAgent = domain.userAgent
        )
    }
}
