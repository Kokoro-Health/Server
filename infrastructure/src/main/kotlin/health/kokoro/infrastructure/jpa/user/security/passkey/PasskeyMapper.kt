package health.kokoro.infrastructure.jpa.user.security.passkey

import health.kokoro.domain.model.user.security.passkey.Passkey
import health.kokoro.domain.model.user.security.passkey.PasskeyChallenge
import health.kokoro.domain.port.security.EncryptionPort
import health.kokoro.infrastructure.jpa.user.UserEntity
import health.kokoro.infrastructure.jpa.user.UserJpaRepository
import org.springframework.stereotype.Component

@Component
class PasskeyMapper(
    private val userJpaRepository: UserJpaRepository,
    private val encryptionPort: EncryptionPort
) {
    fun toEntity(passkey: Passkey): PasskeyEntity {
        val user = userJpaRepository.findById(passkey.userId).orElseThrow()
        return PasskeyEntity(
            user = user,
            credentialId = passkey.credentialId,
            publicKey = passkey.publicKey,
            signCount = passkey.signCount,
            deviceName = encryptionPort.encrypt(passkey.deviceName),
            lastUsedAt = passkey.lastUsedAt
        )
    }

    fun toDomain(passKeyEntity: PasskeyEntity): Passkey {
        return Passkey(
            id = passKeyEntity.id!!,
            userId = passKeyEntity.user.id!!,
            credentialId = passKeyEntity.credentialId,
            publicKey = passKeyEntity.publicKey,
            signCount = passKeyEntity.signCount,
            deviceName = encryptionPort.decrypt(passKeyEntity.deviceName),
            createdAt = passKeyEntity.createdAt,
            lastUsedAt = passKeyEntity.lastUsedAt
        )
    }

    fun toEntity(domain: PasskeyChallenge): PasskeyChallengeEntity {
        var user: UserEntity? = null
        if (domain.userId != null) {
            user = userJpaRepository.findById(domain.userId!!).orElseThrow()
        }
        return PasskeyChallengeEntity(
            user = user,
            email = domain.email,
            type = domain.type,
            data = encryptionPort.encrypt(domain.data),
            expiresAt = domain.expiresAt
        )
    }

    fun toDomain(entity: PasskeyChallengeEntity): PasskeyChallenge {
        return PasskeyChallenge(
            id = entity.id!!,
            userId = entity.user?.id!!,
            email = entity.email,
            data = encryptionPort.decrypt(entity.data),
            type = entity.type,
            expiresAt = entity.expiresAt,
            createdAt = entity.createdAt
        )
    }
}