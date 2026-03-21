package health.kokoro.application.usecase.energy

import health.kokoro.domain.model.user.User
import health.kokoro.domain.port.energy.EnergyEntryRepository
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

@Service
class GetEnergyEntries(
    private val energyRepo: EnergyEntryRepository
) {
    fun getReasonById(userId: UUID, entryId: UUID): Response {
        val entry = energyRepo.findById(entryId) ?: throw IllegalArgumentException("Entry not found")
        if (entry.userId !== userId) throw IllegalArgumentException("Entry not found")
        return Response(date = entry.createdAt, reason = entry.reason, amount = entry.amount , id = entry.id)
    }

    fun getAverageToday(user: User): Int {
        val zone = user.settings.timeZone
        val entries = energyRepo.findAllByUserSince(user.id!!, getStartOfDay(zone, Instant.now()))

        if (entries.isEmpty()) return 0

        return entries.sumOf { it.amount } / entries.size
    }

    fun getForDateRange(
        user: User,
        from: Instant,
        to: Instant,
    ): List<Response> {
        val zone = user.settings.timeZone

        val endOfDay = { instant: Instant ->
            instant.atZone(zone)
                .toLocalDate()
                .atStartOfDay(zone)
                .plusDays(1)
                .minusNanos(1)
                .toInstant()
        }
        val from = getStartOfDay(zone,from)
        val to = endOfDay(to)
        validateDateRange(from, to)

        val entries = energyRepo.findAllByUserInRange(user.id!!, from, to)
        if (entries.isEmpty()) return emptyList()

        return entries
            .groupBy { getStartOfDay(zone, it.createdAt) }
            .map { (dayStart, dayEntries) ->
                val sum = dayEntries.sumOf { it.amount.toLong() }
                val count = dayEntries.size

                val average = sum / count

                Response(
                    id = null,
                    date = dayStart,
                    amount = average.toInt(),
                    reason = null
                )
            }
            .sortedBy { it.date }
    }

    fun  getStartOfDay(zone: ZoneId, instant: Instant): Instant {
       return instant.atZone(zone).toLocalDate().atStartOfDay(zone).toInstant()
    }

    private fun validateDateRange(from: Instant, to: Instant) {
        val now = Instant.now()
        val oneYearAgo = now.minus(365, ChronoUnit.DAYS)

        require(to <= now.plusSeconds(86400)) { "End date cannot be in the future" }
        require(from >= oneYearAgo) { "Start date cannot be more than one year in the past" }
        require(from <= to) { "Start date must be before or equal to end date" }
    }

    data class Response(
        val id: UUID?,
        val date: Instant,
        val reason: String?,
        val amount: Int
    )
}