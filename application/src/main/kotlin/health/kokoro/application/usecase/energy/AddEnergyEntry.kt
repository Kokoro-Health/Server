package health.kokoro.application.usecase.energy

import health.kokoro.domain.model.energy.EnergyEntry
import health.kokoro.domain.model.user.User
import health.kokoro.domain.port.energy.EnergyEntryRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

@Service
class AddEnergyEntry(
    private val repo: EnergyEntryRepository, private val nextEntry: GetNextEntryAllowedDate
) {
    fun execute(amount: Int, reason: String?, user: User) {
        val userId = user.id ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        val latest = repo.findLatestByUser(userId)
        val now = Instant.now()
        if (latest != null && nextEntry.execute(userId).nextEntryAllowedAt.isAfter(now)) {
            throw ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS)
        }

        repo.save(
            EnergyEntry(
                id = null, amount = amount, createdAt = now, reason = reason, userId = userId
            )
        )
    }

}