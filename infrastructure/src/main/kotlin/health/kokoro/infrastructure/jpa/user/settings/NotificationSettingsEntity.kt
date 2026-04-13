package health.kokoro.infrastructure.jpa.user.settings

import health.kokoro.infrastructure.jpa.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "notification_settings")
class NotificationSettingsEntity(
    @Column("marketing_emails") var marketingEmails: Boolean,
    @Column("security_alerts") var securityAlerts: Boolean,
    @Column("reminder_emails") var reminderEmails: Boolean
) : BaseEntity()
