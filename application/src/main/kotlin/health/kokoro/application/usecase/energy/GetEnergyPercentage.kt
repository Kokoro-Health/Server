package health.kokoro.application.usecase.energy

import health.kokoro.domain.port.energy.EnergyEntryRepository
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

@Service
class GetEnergyPercentage(
    private val energyRepo: EnergyEntryRepository,
    private val clock: Clock
) {
    private val ZERO_INT = 0
    private val zone = ZoneId.systemDefault()

    fun execute(uuid: UUID): Int {
        val todayStart = Instant.now(clock).atZone(zone).toLocalDate().atStartOfDay(zone).toInstant()
        val entries = energyRepo.findAllByUserSince(uuid, todayStart)

        if (entries.isEmpty()) return ZERO_INT

        return entries.sumOf { it.amount } / entries.size
    }

    fun executeInDateRange(
        uuid: UUID,
        from: Instant,
        to: Instant,
    ): List<Response> {
        validateDateRange(from, to)

        val entries = energyRepo.findAllByUserInRange(uuid, from, to)
        if (entries.isEmpty()) return emptyList()

        return entries
            .groupBy { it.createdAt.atZone(zone).toLocalDate() }
            .map { (date, dayEntries) ->
                Response(
                    date = date,
                    amount = dayEntries.sumOf { it.amount } / dayEntries.size
                )
            }
            .sortedBy { it.date }
    }

    private fun validateDateRange(from: Instant, to: Instant) {
        val now = Instant.now(clock)
        val oneYearAgo = now.minus(365, ChronoUnit.DAYS)

        require(to <= now.plusSeconds(86400)) { "End date cannot be in the future" }
        require(from >= oneYearAgo) { "Start date cannot be more than one year in the past" }
        require(from <= to) { "Start date must be before or equal to end date" }
    }

    data class Response(
        val date: LocalDate,
        val amount: Int
    )
}