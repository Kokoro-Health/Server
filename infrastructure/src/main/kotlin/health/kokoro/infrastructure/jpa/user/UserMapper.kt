package health.kokoro.infrastructure.jpa.user

import health.kokoro.domain.model.user.User
import health.kokoro.domain.model.user.Role
import health.kokoro.domain.port.security.EncryptionPort
import health.kokoro.infrastructure.jpa.file.FileUploadJpaRepository
import health.kokoro.infrastructure.jpa.file.FileUploadMapper
import health.kokoro.infrastructure.jpa.user.security.UserSecurityMapper
import health.kokoro.infrastructure.jpa.user.settings.SettingsMapper
import org.springframework.stereotype.Component

@Component
class UserMapper(
    private val securityMapper: UserSecurityMapper,
    private val settingsMapper: SettingsMapper,
    private val encryptionPort: EncryptionPort,
    private val fileMapper: FileUploadMapper,
    private val fileUploadJpaRepository: FileUploadJpaRepository
) {
    fun toDomain(user: UserEntity): User {
        return User(
            id = user.id!!,
            firstName = encryptionPort.decrypt(user.firstName),
            middleName = user.middleName?.let { encryptionPort.decrypt(it) },
            lastName = encryptionPort.decrypt(user.lastName),
            email = user.email,
            security = securityMapper.toDomain(user.security),
            settings = settingsMapper.toDomain(user.settings),
            createdAt = user.createdAt,
            updatedAt = user.updatedAt,
            profilePicture = user.profilePicture?.let { fileMapper.toDomain(it) },
            enabled = user.enabled,
            role = user.role
        )
    }

    fun toEntity(user: User): UserEntity {
        val userEntity = UserEntity(
            firstName = encryptionPort.encrypt(user.firstName),
            middleName = user.middleName?.let { encryptionPort.encrypt(it) },
            lastName = encryptionPort.encrypt(user.lastName),
            email = user.email,
            security = securityMapper.toEntity(user.security),
            settings = settingsMapper.toEntity(user.settings),
            profilePicture = user.profilePicture?.let { fileUploadJpaRepository.findById(it.id).get() },
            enabled = user.enabled,
            role = user.role
        )
        userEntity.id = user.id
        userEntity.createdAt = user.createdAt
        userEntity.updatedAt = user.updatedAt
        return userEntity
    }
}