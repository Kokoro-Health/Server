package health.kokoro.application.usecase.user.privacy

import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import health.kokoro.application.usecase.util.RequestDetails
import health.kokoro.domain.error.DataExportRateLimitedException
import health.kokoro.domain.model.audit.AuditAction
import health.kokoro.domain.model.audit.AuditEvent
import health.kokoro.domain.model.user.User
import health.kokoro.domain.model.user.privacy.DataExportRecord
import health.kokoro.domain.model.user.privacy.DataExportStatus
import health.kokoro.domain.port.audit.AuditEventRepository
import health.kokoro.domain.port.energy.EnergyEntryRepository
import health.kokoro.domain.port.journal.JournalRepository
import health.kokoro.domain.port.mail.MailSenderRepository
import health.kokoro.domain.port.user.UserRepository
import health.kokoro.domain.port.user.privacy.DataExportRepository
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class ExportUserData(
    private val userRepository: UserRepository,
    private val journalRepository: JournalRepository,
    private val energyRepository: EnergyEntryRepository,
    private val dataExportRepository: DataExportRepository,
    private val mailSender: MailSenderRepository,
    private val auditLog: AuditEventRepository
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    private val gson = GsonBuilder()
        .serializeNulls()
        .setPrettyPrinting()
        .registerTypeAdapter(Instant::class.java, object : TypeAdapter<Instant>() {
            private val formatter = DateTimeFormatter.ISO_INSTANT
            override fun write(out: JsonWriter, value: Instant?) {
                if (value == null) {
                    out.nullValue()
                } else {
                    out.value(formatter.format(value))
                }
            }

            override fun read(`in`: JsonReader): Instant? {
                return if (`in`.peek() == com.google.gson.stream.JsonToken.NULL) {
                    `in`.nextNull()
                    null
                } else {
                    Instant.parse(`in`.nextString())
                }
            }
        })
        .create()

    fun requestExport(user: User, req: HttpServletRequest): DataExportRecord {
        val userId = user.id ?: throw IllegalStateException("User ID is null")

        val recentExport = dataExportRepository.findByUserId(userId)
            .filter { it.status == DataExportStatus.COMPLETED }
            .maxByOrNull { it.completedAt ?: Instant.MIN }

        if (recentExport != null) {
            val hoursSinceLastExport = java.time.Duration.between(recentExport.completedAt, Instant.now()).toHours()
            if (hoursSinceLastExport < 24) {
                throw DataExportRateLimitedException("Data export was requested within the last 24 hours. Please try again later.")
            }
        }

        val record = DataExportRecord(
            id = UUID.randomUUID(),
            userId = userId,
            requestedAt = Instant.now(),
            completedAt = null,
            status = DataExportStatus.PENDING,
            ipAddress = "",
            userAgent = ""
        )

        val saved = dataExportRepository.save(record)
        processExportAsync(saved.id!!, userId, user.email)
        addAuditLog(user, req)
        return saved
    }

    @Async
    fun processExportAsync(exportId: UUID, userId: UUID, email: String) {
        val record = dataExportRepository.findById(exportId) ?: return

        try {
            setProcessingStatus(record)

            val user = userRepository.findById(userId) ?: return
            val export = buildExportData(user)
            val jsonBytes = gson.toJson(export).toByteArray()
            mailSender.sendTemplateWithAttachment(
                to = email,
                subject = "Your Kokoro Data Export",
                template = "data-export",
                model = mapOf("year" to LocalDate.now().year),
                attachmentName = "kokoro-data-export-$exportId.json",
                attachmentData = jsonBytes,
                attachmentMimeType = "application/json"
            )

            val completed = record.copy(status = DataExportStatus.COMPLETED, completedAt = Instant.now())
            dataExportRepository.update(completed)

            logger.info("Data export completed for user $userId, export ID: $exportId")

        } catch (e: Exception) {
            logger.error("Data export failed for export ID: $exportId", e)
            dataExportRepository.update(record.copy(status = DataExportStatus.FAILED))
        }
    }

    private fun buildExportData(user: User): UserDataExport {
        val userId = user.id ?: throw IllegalStateException("User ID is null")

        return UserDataExport(
            metadata = ExportMetadata(
                exportedAt = Instant.now(),
                version = "1.0",
                userId = userId
            ),
            profile = ProfileExport(
                id = userId,
                email = user.email,
                firstName = user.firstName,
                middleName = user.middleName,
                lastName = user.lastName,
                timezone = user.settings.timeZone.id.toString(),
                dateFormat = user.settings.dateFormat,
                language = user.settings.language.name,
                theme = user.settings.theme.name,
                verified = user.security.verified,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt
            ),
            notificationSettings = NotificationSettingsExport(
                marketingEmails = user.settings.notificationSettings.marketingEmails,
                securityAlerts = user.settings.notificationSettings.securityAlerts,
                reminderEmails = user.settings.notificationSettings.reminderEmails
            ),
            journals = journalRepository.getAllByUserId(userId).map { journal ->
                JournalExport(
                    id = journal.id ?: throw IllegalStateException("Journal ID is null"),
                    content = journal.content,
                    createdAt = journal.createdAt,
                    updatedAt = journal.updatedAt
                )
            },
            energyEntries = energyRepository.findAllByUser(userId).map { entry ->
                EnergyEntryExport(
                    id = entry.id ?: throw IllegalStateException("Energy entry ID is null"),
                    amount = entry.amount,
                    reason = entry.reason,
                    createdAt = entry.createdAt
                )
            },
            security = SecurityExport(
                mfaEnabled = user.security.mfaEnabled,
                registeredAt = user.security.passwordResetCodeRequestedAt ?: user.createdAt
            )
        )
    }

    private fun setProcessingStatus(record: DataExportRecord) {
        dataExportRepository.update(record.copy(status = DataExportStatus.PROCESSING))
    }

    fun addAuditLog(user: User, request: HttpServletRequest) {
        val details = RequestDetails(request)

        val event = AuditEvent(
            id = UUID.randomUUID(),
            userId = user.id!!,
            action = AuditAction.DATA_EXPORT,
            userAgent = details.getUserAgent(),
            ipAddress = details.getIpAddress(),
            metaData = null,
            timeStamp = Instant.now()
        )
        auditLog.add(event)
    }

    data class UserDataExport(
        val metadata: ExportMetadata,
        val profile: ProfileExport,
        val notificationSettings: NotificationSettingsExport,
        val journals: List<JournalExport>,
        val energyEntries: List<EnergyEntryExport>,
        val security: SecurityExport
    )

    data class ExportMetadata(
        val exportedAt: Instant,
        val version: String,
        val userId: UUID
    )

    data class ProfileExport(
        val id: UUID,
        val email: String,
        val firstName: String,
        val middleName: String?,
        val lastName: String,
        val timezone: String,
        val dateFormat: String,
        val language: String,
        val theme: String,
        val verified: Boolean,
        val createdAt: Instant,
        val updatedAt: Instant
    )

    data class NotificationSettingsExport(
        val marketingEmails: Boolean,
        val securityAlerts: Boolean,
        val reminderEmails: Boolean
    )

    data class JournalExport(
        val id: UUID,
        val content: String,
        val createdAt: Instant,
        val updatedAt: Instant
    )

    data class EnergyEntryExport(
        val id: UUID,
        val amount: Int,
        val reason: String?,
        val createdAt: Instant
    )

    data class SecurityExport(
        val mfaEnabled: Boolean,
        val registeredAt: Instant
    )
}
