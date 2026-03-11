package health.kokoro.domain.model.user.settings

import java.util.*

data class NotificationSettings(
    val id: UUID? = null,
    var marketingEmails: Boolean,
    var securityAlerts: Boolean,
    var reminderEmails: Boolean
)
