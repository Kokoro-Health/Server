package health.kokoro.application.usecase.energy

import health.kokoro.domain.model.energy.EnergyEntry
import health.kokoro.domain.model.user.User
import health.kokoro.domain.port.energy.EnergyEntryRepository
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class AddEnergyEntry(
    private val repo: EnergyEntryRepository, private val nextEntry: GetNextEntryAllowedDate
) {
    fun execute(amount: Int, reason: String?, user: User) {
        val userId = user.id!!
        val latest = repo.findLatestByUser(userId)
        val now = Instant.now()
        if (latest != null && nextEntry.execute(userId).nextEntryAllowedAt.isAfter(now)) {
            throw IllegalStateException("Wait for the cooldown to expire.")
        }

        repo.save(
            EnergyEntry(
                id = null, amount = amount, createdAt = now, reason = reason, userId = userId
            )
        )
    }

}