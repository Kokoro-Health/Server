package health.kokoro.infrastructure.jpa.user.settings

import health.kokoro.domain.model.user.settings.LanguageSetting
import health.kokoro.domain.model.user.settings.ThemeSetting
import health.kokoro.infrastructure.jpa.BaseEntity
import health.kokoro.infrastructure.jpa.user.UserEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "settings")
data class SettingsEntity(
    @JoinColumn(name = "user_id") @OneToOne var user: UserEntity,
    @Enumerated(EnumType.STRING) @Column("language") var language: LanguageSetting,
    @Enumerated(EnumType.STRING) @Column("theme") var theme: ThemeSetting,
    @JoinColumn(name = "notification_settings_id") @OneToOne var notificationSettings: NotificationSettingsEntity
    ): BaseEntity()