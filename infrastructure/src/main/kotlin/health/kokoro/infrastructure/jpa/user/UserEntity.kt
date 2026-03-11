package health.kokoro.infrastructure.jpa.user

import health.kokoro.infrastructure.jpa.BaseEntity
import health.kokoro.infrastructure.jpa.user.security.UserSecurityEntity
import health.kokoro.infrastructure.jpa.user.settings.SettingsEntity
import jakarta.persistence.*

@Entity
@Table(name = "users")
class UserEntity(
    @Column(name = "first_name", nullable = false) var firstName: String,
    @Column(name = "middle_name") var middleName: String?,
    @Column(name = "last_name", nullable = false) var lastName: String,
    @Column(name = "email", nullable = false, unique = true) var email: String,
    @Column(name = "profile_picture_url") var profilePictureUrl: String?,
    @JoinColumn(
        "security_id",
        nullable = false
    ) @OneToOne(cascade = [CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE]) var security: UserSecurityEntity,
    @JoinColumn("settings_id") @OneToOne(cascade = [CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE]) var settings: SettingsEntity
) : BaseEntity()