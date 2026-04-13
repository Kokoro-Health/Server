package health.kokoro.application.usecase.user.deletion

import health.kokoro.domain.port.audit.AuditEventRepository
import health.kokoro.domain.port.data.DataDeletionRequestRepository
import health.kokoro.domain.port.energy.EnergyEntryRepository
import health.kokoro.domain.port.journal.JournalRepository
import health.kokoro.domain.port.user.passkey.PasskeyChallengeRepository
import health.kokoro.domain.port.user.passkey.PasskeyRepository
import health.kokoro.domain.port.user.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class ExecuteDataDeletion(
    private val userRepository: UserRepository,
    private val deletionRequestRepository: DataDeletionRequestRepository,
    private val journalRepository: JournalRepository,
    private val energyRepository: EnergyEntryRepository,
    private val passkeyRepository: PasskeyRepository,
    private val passkeyChallengeRepository: PasskeyChallengeRepository,
    private val auditEventRepository: AuditEventRepository
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Scheduled(cron = "0 0 2 * * ?")
    fun executeScheduled() {
        execute()
    }

    @Transactional
    fun execute() {
        val now = Instant.now()
        val deletableDate = now.minus(7, ChronoUnit.DAYS)

        val requests = deletionRequestRepository.findDeletableRequests(deletableDate)

        for (request in requests) {
            try {
                deleteUserData(request.userId)
                deletionRequestRepository.deleteByUserId(request.userId)
                logger.info("Successfully deleted data for user ${request.userId}")
            } catch (e: Exception) {
                logger.error("Failed to delete data for user ${request.userId}", e)
            }
        }
    }

    private fun deleteUserData(userId: java.util.UUID) {
        auditEventRepository.deleteAllByUserId(userId)
        passkeyChallengeRepository.deleteAllByUserId(userId)
        passkeyRepository.deleteAllByUserId(userId)
        journalRepository.deleteAllByUserId(userId)
        energyRepository.deleteAllByUserId(userId)
        deletionRequestRepository.deleteByUserId(userId)
        userRepository.deleteById(userId)
    }
}