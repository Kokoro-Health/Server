package health.kokoro.application.usecase.energy

import health.kokoro.domain.model.energy.EnergyEntry
import health.kokoro.domain.model.user.User
import health.kokoro.domain.port.energy.EnergyEntryRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

@Service
class GetEnergyEntries(
    private val energyRepo: EnergyEntryRepository
) {
    fun getDetails(user: User, date: Instant): DetailResponse {
        val zone = user.settings.timeZone
        val startZdt = getStartOfDay(zone, date).atZone(zone)
        val endZdt = startZdt.plusDays(1).minusNanos(1)

        val start = startZdt.toInstant()
        val end = endZdt.toInstant()

        val entries = energyRepo.findAllByUserInRange(user.id!!, start, end)

        val responses = mapToResponses(entries)

        val netReasons = calculateNetReasons(entries)

        val influentialPositive =
            netReasons.filter { it.value > 50 }.maxByOrNull { it.value }?.let { (reason, amount) ->
                ReasonAmount(reason, amount.toInt())
            }

        val influentialNegative =
            netReasons.filter { it.value <= 50 }.minByOrNull { it.value }?.let { (reason, amount) ->
                ReasonAmount(reason, amount.toInt())
            }

        val average = calculateAverage(entries)

        return DetailResponse(
            influentialPositive = influentialPositive ?: ReasonAmount("None", 0),
            influentialNegative = influentialNegative ?: ReasonAmount("None", 0),
            average = average,
            entries = responses.sortedByDescending { it.date }
        )
    }

    private fun mapToResponses(entries: List<EnergyEntry>): List<Response> {
        return entries.map {
            Response(
                id = it.id,
                date = it.createdAt,
                amount = it.amount,
                reason = it.reason
            )
        }
    }

    private fun calculateNetReasons(entries: List<EnergyEntry>): Map<String, Long> {
        return entries
            .filter { it.reason != null }
            .groupBy { it.reason!! }
            .mapValues { (_, group) ->
                group.sumOf { entry ->
                    val normalizedAmount = normalizeAmount(entry.amount)
                    normalizedAmount.toLong()
                }
            }
    }

    private fun normalizeAmount(amount: Int): Int {
        return if (amount > 50) {
            amount
        } else {
            -(50 - amount)
        }
    }

    private fun calculateAverage(entries: List<EnergyEntry>): Int {
        if (entries.isEmpty()) return 50
        return ((entries.sumOf { it.amount.toLong() } + 50) / (entries.size + 1)).toInt()
    }

    fun getAverageToday(user: User): Int {
        val zone = user.settings.timeZone
        val entries = energyRepo.findAllByUserSince(user.id!!, getStartOfDay(zone, Instant.now()))

        return calculateAverage(entries)
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
        val from = getStartOfDay(zone, from)
        val to = endOfDay(to)
        validateDateRange(from, to)

        val entries = energyRepo.findAllByUserInRange(user.id!!, from, to)
        if (entries.isEmpty()) return emptyList()

        return entries
            .groupBy { getStartOfDay(zone, it.createdAt) }
            .map { (dayStart, dayEntries) ->
                val average = calculateAverage(dayEntries)

                Response(
                    id = null,
                    date = dayStart,
                    amount = average,
                    reason = null
                )
            }
            .sortedBy { it.date }
    }

    fun getStartOfDay(zone: ZoneId, instant: Instant): Instant {
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

    data class DetailResponse(
        val influentialPositive: ReasonAmount,
        val influentialNegative: ReasonAmount,
        val average: Int,
        val entries: List<Response>
    )

    data class ReasonAmount(
        val reason: String,
        val level: Int
    )
}