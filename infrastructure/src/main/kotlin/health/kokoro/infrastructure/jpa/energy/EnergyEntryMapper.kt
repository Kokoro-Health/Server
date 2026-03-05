package health.kokoro.infrastructure.jpa.energy

import health.kokoro.domain.model.EnergyEntry
import org.springframework.stereotype.Component

@Component
class EnergyEntryMapper {
    fun toDomain(entity: EnergyEntryEntity): EnergyEntry {
        return EnergyEntry(
            id = entity.id,
            amount = entity.amount,
            userId = entity.user.id!!,
            createdAt = entity.createdAt,
            reason = entity.reason
        )
    }
}