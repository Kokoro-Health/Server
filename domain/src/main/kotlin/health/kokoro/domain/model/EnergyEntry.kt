package health.kokoro.domain.model

import java.time.Instant
import java.util.UUID

data class EnergyEntry(
    val id: UUID?,
    val amount: Int,
    val createdAt: Instant,
    val reason: String?,
    val userId: UUID
)
