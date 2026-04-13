package health.kokoro.infrastructure.jpa.energy

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*

@Repository
interface EnergyEntryJpaRepository : JpaRepository<EnergyEntryEntity, UUID> {
    fun findAllByUserIdOrderByCreatedAtDesc(uuid: UUID): List<EnergyEntryEntity>
    fun findAllByUserIdAndCreatedAtGreaterThanEqualOrderByCreatedAtDesc(
        uuid: UUID,
        since: Instant
    ): List<EnergyEntryEntity>

    fun findAllByUserIdAndCreatedAtBetween(uuid: UUID, from: Instant, to: Instant): List<EnergyEntryEntity>

    fun deleteAllByUserId(userId: UUID)
}