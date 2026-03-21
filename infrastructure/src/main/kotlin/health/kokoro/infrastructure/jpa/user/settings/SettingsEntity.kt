package health.kokoro.infrastructure.jpa.user.settings

import health.kokoro.domain.model.user.settings.LanguageSetting
import health.kokoro.domain.model.user.settings.ThemeSetting
import health.kokoro.infrastructure.converter.ZoneIdConverter
import health.kokoro.infrastructure.jpa.BaseEntity
import health.kokoro.infrastructure.jpa.user.UserEntity
import jakarta.persistence.*
import java.time.ZoneId

@Entity
@Table(name = "settings")
class SettingsEntity(
    @Enumerated(EnumType.STRING) @Column("language") var language: LanguageSetting,
    @Enumerated(EnumType.STRING) @Column("theme") var theme: ThemeSetting,
    @Column("timezone") @Convert(converter = ZoneIdConverter::class) var timezone: ZoneId,
    @Column("date_format") var dateFormat: String,
    @JoinColumn(name = "notification_settings_id") @OneToOne(cascade = [CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE]) var notificationSettings: NotificationSettingsEntity,
    @OneToOne(mappedBy = "settings") var user: UserEntity? = null
) : BaseEntity()