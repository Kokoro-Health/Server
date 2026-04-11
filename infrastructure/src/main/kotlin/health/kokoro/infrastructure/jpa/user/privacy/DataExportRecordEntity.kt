package health.kokoro.infrastructure.jpa.user.privacy

import health.kokoro.domain.model.user.privacy.DataExportStatus
import jakarta.persistence.*
import java.time.Instant
import java.util.*

@Entity
@Table(name = "data_export_records")
data class DataExportRecordEntity(
    @Id
    @Column(name = "id") var id: UUID,

    @Column(name = "user_id", nullable = false)
    var userId: UUID,

    @Column(name = "requested_at", nullable = false)
    var requestedAt: Instant,

    @Column(name = "completed_at")
    var completedAt: Instant?,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: DataExportStatus,

    @Column(name = "ip_address")
    var ipAddress: String,

    @Column(name = "user_agent")
    var userAgent: String
)
