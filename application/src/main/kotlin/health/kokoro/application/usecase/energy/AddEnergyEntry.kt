package health.kokoro.application.usecase.energy

import health.kokoro.domain.model.EnergyEntry
import health.kokoro.domain.model.User
import health.kokoro.domain.port.EnergyEntryRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.Clock
import java.time.Instant

@Service
class AddEnergyEntry(
    private val repo: EnergyEntryRepository, private val clock: Clock, private val nextEntry: GetNextEntryAllowedDate
) {
    fun execute(amount: Int, user: User) {
        val userId = user.id ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        val latest = repo.findLatestByUser(userId)
        val now = Instant.now(clock)
        if (latest != null && nextEntry.execute(userId).nextEntryAllowedAt.isAfter(now)) {
            throw ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS)
        }
        repo.save(
            EnergyEntry(
                id = null, amount = amount, createdAt = now, reason = null, userId = userId
            )
        )
    }

}