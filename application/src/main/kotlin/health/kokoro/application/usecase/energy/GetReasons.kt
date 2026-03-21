package health.kokoro.application.usecase.energy

import health.kokoro.domain.model.user.User
import health.kokoro.domain.port.energy.EnergyEntryRepository
import org.springframework.stereotype.Service

@Service
class GetReasons(
    private val energyEntryRepository: EnergyEntryRepository,
) {
    fun execute(user: User): List<String> {
        val userId = user.id ?: return REASONS.toList()
        val userReasons = energyEntryRepository.findReasonsByUserId(userId)
        val missingCount = REASONS.size - userReasons.size

        if (missingCount <= 0) return userReasons
        val defaultsToAdd = REASONS.filter { it !in userReasons }.take(missingCount)
        return userReasons + defaultsToAdd
    }

    companion object {
        private val REASONS = listOf(
            "Work",
            "Study",
            "Relaxation",
            "Exercise",
            "Party"
        )
    }
}