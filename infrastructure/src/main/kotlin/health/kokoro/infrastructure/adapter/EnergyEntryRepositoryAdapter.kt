package health.kokoro.infrastructure.adapter

import health.kokoro.domain.model.energy.EnergyEntry
import health.kokoro.domain.port.energy.EnergyEntryRepository
import health.kokoro.infrastructure.jpa.energy.EnergyEntryJpaRepository
import health.kokoro.infrastructure.jpa.energy.EnergyEntryMapper
import health.kokoro.infrastructure.jpa.user.UserJpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class EnergyEntryRepositoryAdapter(
    private var jpa: EnergyEntryJpaRepository,
    private var mapper: EnergyEntryMapper,
    private var userJpa: UserJpaRepository
) : EnergyEntryRepository {
    override fun findAllByUser(uuid: UUID): List<EnergyEntry> {
        if (!userJpa.existsById(uuid)) throw IllegalArgumentException("Could not find user with id ${uuid.toString()}")
        return jpa.findAllByUserId(uuid).map { mapper.toDomain(it) }
    }

    override fun save(entry: EnergyEntry) {
       jpa.save(mapper.toEntity(entry))
    }

    override fun findLatestByUser(uuid: UUID): EnergyEntry? {
        return jpa.findAllByUserId(uuid).maxByOrNull { it.createdAt }?.let { mapper.toDomain(it) }
    }
}