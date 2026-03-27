package health.kokoro.application.usecase.streak

import health.kokoro.domain.model.user.User
import health.kokoro.domain.port.energy.EnergyEntryRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Service
class GetCurrentStreak(
    private val entryRepo: EnergyEntryRepository
) {
    fun execute(user: User): Response {
        val allJournals = entryRepo.findAllByUser(user.id!!)
        if (allJournals.isEmpty()) {
            return Response(0, false)
        }

        val journalDates = allJournals
            .map { it.createdAt.atZone(user.settings.timeZone).toLocalDate() }
            .distinct()
            .sortedDescending()

        val today = LocalDate.now(user.settings.timeZone)
        val yesterday = today.minusDays(1)

        val hasEntryToday = journalDates.any { it == today }

        if (!hasEntryToday && journalDates.first() != yesterday) {
            return Response(0, false)
        }

        var currentStreak = 1
        var previousDate = journalDates.first()

        for (i in 1 until journalDates.size) {
            val currentDate = journalDates[i]
            val daysBetween = ChronoUnit.DAYS.between(currentDate, previousDate)

            when {
                daysBetween == 1L -> {
                    currentStreak++
                    previousDate = currentDate
                }

                daysBetween > 1L -> break
            }
        }

        return Response(currentStreak, hasEntryToday)
    }

    data class Response(
        val currentStreak: Int,
        val streakIncreasedToday: Boolean
    )
}
