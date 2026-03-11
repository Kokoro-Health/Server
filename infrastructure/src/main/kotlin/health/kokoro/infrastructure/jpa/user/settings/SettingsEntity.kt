package health.kokoro.infrastructure.jpa.user.settings

import health.kokoro.domain.model.user.settings.LanguageSetting
import health.kokoro.domain.model.user.settings.ThemeSetting
import health.kokoro.infrastructure.jpa.BaseEntity
import health.kokoro.infrastructure.jpa.user.UserEntity
import jakarta.persistence.*

@Entity
@Table(name = "settings")
class SettingsEntity(
    @Enumerated(EnumType.STRING) @Column("language") var language: LanguageSetting,
    @Enumerated(EnumType.STRING) @Column("theme") var theme: ThemeSetting,
    @JoinColumn(name = "notification_settings_id") @OneToOne(cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE]) var notificationSettings: NotificationSettingsEntity,
    @OneToOne(mappedBy = "settings") var user: UserEntity? = null
) : BaseEntity()