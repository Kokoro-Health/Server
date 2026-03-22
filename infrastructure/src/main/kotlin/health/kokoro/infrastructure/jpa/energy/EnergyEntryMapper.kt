package health.kokoro.infrastructure.jpa.energy

import health.kokoro.domain.model.energy.EnergyEntry
import health.kokoro.domain.port.security.EncryptionPort
import health.kokoro.infrastructure.jpa.user.UserJpaRepository
import org.springframework.stereotype.Component

@Component
class EnergyEntryMapper(
    private val userJpaRepository: UserJpaRepository,
    private val encryptionPort: EncryptionPort
) {
    fun toDomain(entity: EnergyEntryEntity): EnergyEntry {
        return EnergyEntry(
            id = entity.id,
            amount = entity.amount,
            userId = entity.user.id!!,
            createdAt = entity.createdAt,
            reason = entity.reason?.let { encryptionPort.decrypt(it) }
        )
    }

    fun toEntity(domain: EnergyEntry): EnergyEntryEntity {
        return EnergyEntryEntity(
            amount = domain.amount,
            user = userJpaRepository.findById(domain.userId).get(),
            reason = domain.reason?.let { encryptionPort.encrypt(it) }
        )
    }
}