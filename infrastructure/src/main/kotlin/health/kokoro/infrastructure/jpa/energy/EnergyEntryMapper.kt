package health.kokoro.infrastructure.jpa.energy

import health.kokoro.domain.model.EnergyEntry
import health.kokoro.infrastructure.jpa.user.UserJpaRepository
import org.springframework.stereotype.Component

@Component
class EnergyEntryMapper(
    private val userJpaRepository: UserJpaRepository
) {
    fun toDomain(entity: EnergyEntryEntity): EnergyEntry {
        return EnergyEntry(
            id = entity.id,
            amount = entity.amount,
            userId = entity.user.id!!,
            createdAt = entity.createdAt,
            reason = entity.reason
        )
    }
    fun toEntity(domain: EnergyEntry): EnergyEntryEntity {
        return EnergyEntryEntity(
            amount = domain.amount,
            user = userJpaRepository.findById(domain.userId).get(),
            reason = domain.reason
        )
    }
}