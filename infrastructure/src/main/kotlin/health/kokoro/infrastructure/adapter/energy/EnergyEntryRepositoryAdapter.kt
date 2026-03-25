package health.kokoro.infrastructure.adapter.energy

import health.kokoro.domain.model.energy.EnergyEntry
import health.kokoro.domain.port.energy.EnergyEntryRepository
import health.kokoro.domain.port.security.EncryptionPort
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
    private var userJpa: UserJpaRepository,
    private var encryptionPort: EncryptionPort
) : EnergyEntryRepository {
    override fun findAllByUser(uuid: UUID): List<EnergyEntry> {
        validateUserExists(uuid)
        return jpa.findAllByUserIdOrderByCreatedAtDesc(uuid).map { mapper.toDomain(it) }
    }

    override fun save(entry: EnergyEntry) {
        jpa.save(mapper.toEntity(entry))
    }

    override fun findAllByUserSince(uuid: UUID, since: Instant): List<EnergyEntry> {
        validateUserExists(uuid)
        return jpa.findAllByUserIdAndCreatedAtGreaterThanEqualOrderByCreatedAtDesc(uuid, since)
            .map { mapper.toDomain(it) }
    }

    override fun findAllByUserInRange(uuid: UUID, from: Instant, to: Instant): List<EnergyEntry> {
        validateUserExists(uuid)
        return jpa.findAllByUserIdAndCreatedAtBetween(uuid, from, to).map { mapper.toDomain(it) }
    }

    override fun findById(uuid: UUID): EnergyEntry? {
        return jpa.findById(uuid).map { mapper.toDomain(it) }.orElse(null)
    }

    override fun findLatestByUser(uuid: UUID): EnergyEntry? {
        return jpa.findAllByUserIdOrderByCreatedAtDesc(uuid).maxByOrNull { it.createdAt }?.let { mapper.toDomain(it) }
    }

    override fun findReasonsByUserId(uuid: UUID): List<String> {
        return jpa.findAllByUserIdOrderByCreatedAtDesc(uuid)
            .mapNotNull { it -> it.reason?.let { encryptionPort.decrypt(it) } }.filter { it.isNotBlank() }
            .distinct()
    }

    private fun validateUserExists(uuid: UUID) {
        if (!userJpa.existsById(uuid)) {
            throw IllegalArgumentException("Could not find user with id $uuid")
        }
    }
}