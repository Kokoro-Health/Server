package health.kokoro.application.usecase.energy

import health.kokoro.domain.port.energy.EnergyEntryRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class GetNextEntryAllowedDate(
    private val repo: EnergyEntryRepository,
) {
    fun execute(userId: UUID): Response {
        val nextEntryAllowedDate = repo.findLatestByUser(userId)?.createdAt?.plusSeconds(60 * 15) ?: Instant.now()
        return Response(nextEntryAllowedDate)
    }

    data class Response(val nextEntryAllowedAt: Instant)
}