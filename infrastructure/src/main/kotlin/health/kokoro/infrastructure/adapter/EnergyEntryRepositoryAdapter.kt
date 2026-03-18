package health.kokoro.infrastructure.adapter

import health.kokoro.domain.model.energy.EnergyEntry
import health.kokoro.domain.port.energy.EnergyEntryRepository
import health.kokoro.infrastructure.jpa.energy.EnergyEntryJpaRepository
import health.kokoro.infrastructure.jpa.energy.EnergyEntryMapper
import health.kokoro.infrastructure.jpa.user.UserJpaRepository
import org.springframework.stereotype.Repository
import java.time.Instant
import java.util.*

@Repository
class EnergyEntryRepositoryAdapter(
    private var jpa: EnergyEntryJpaRepository,
    private var mapper: EnergyEntryMapper,
    private var userJpa: UserJpaRepository
) : EnergyEntryRepository {
    override fun findAllByUser(uuid: UUID): List<EnergyEntry> {
        validateUserExists(uuid)
        return jpa.findAllByUserId(uuid).map { mapper.toDomain(it) }
    }

    override fun save(entry: EnergyEntry) {
        jpa.save(mapper.toEntity(entry))
    }

    override fun findAllByUserSince(uuid: UUID, since: Instant): List<EnergyEntry> {
        validateUserExists(uuid)
        return jpa.findAllByUserIdAndCreatedAtGreaterThanEqual(uuid, since).map { mapper.toDomain(it) }
    }

    override fun findAllByUserInRange(uuid: UUID, from: Instant, to: Instant): List<EnergyEntry> {
        validateUserExists(uuid)
        return jpa.findAllByUserIdAndCreatedAtBetween(uuid, from, to).map { mapper.toDomain(it) }
    }

    override fun findLatestByUser(uuid: UUID): EnergyEntry? {
        return jpa.findAllByUserId(uuid).maxByOrNull { it.createdAt }?.let { mapper.toDomain(it) }
    }

    private fun validateUserExists(uuid: UUID) {
        if (!userJpa.existsById(uuid)) {
            throw IllegalArgumentException("Could not find user with id ${uuid.toString()}")
        }
    }
}