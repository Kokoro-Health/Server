package health.kokoro.infrastructure.jpa.user

import health.kokoro.domain.model.security.EncryptedData
import health.kokoro.infrastructure.converter.EncryptedDataConverter
import health.kokoro.infrastructure.jpa.BaseEntity
import health.kokoro.infrastructure.jpa.file.FileUploadEntity
import health.kokoro.infrastructure.jpa.user.security.UserSecurityEntity
import health.kokoro.infrastructure.jpa.user.settings.SettingsEntity
import jakarta.persistence.*

@Entity
@Table(name = "users")
class UserEntity(
    @Column(
        name = "first_name",
        nullable = false
    ) @Convert(converter = EncryptedDataConverter::class) var firstName: EncryptedData,
    @Column(name = "middle_name") @Convert(converter = EncryptedDataConverter::class) var middleName: EncryptedData?,
    @Column(
        name = "last_name",
        nullable = false
    ) @Convert(converter = EncryptedDataConverter::class) var lastName: EncryptedData,
    @Column(name = "email", nullable = false, unique = true) var email: String,
    @JoinColumn(name = "profile_picture_id") @OneToOne var profilePicture: FileUploadEntity?,
    @JoinColumn(
        "security_id",
        nullable = false
    ) @OneToOne(cascade = [CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE]) var security: UserSecurityEntity,
    @JoinColumn("settings_id") @OneToOne(cascade = [CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.MERGE]) var settings: SettingsEntity
) : BaseEntity()