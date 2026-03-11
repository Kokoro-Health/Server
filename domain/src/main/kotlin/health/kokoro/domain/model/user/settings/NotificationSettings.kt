package health.kokoro.domain.model.user.settings

data class NotificationSettings(
    var marketingEmails: Boolean,
    var securityAlerts: Boolean,
    var reminderEmails: Boolean,
)
