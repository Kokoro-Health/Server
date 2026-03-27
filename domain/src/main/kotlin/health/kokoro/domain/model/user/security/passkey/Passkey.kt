package health.kokoro.domain.model.user.security.passkey

import java.time.Instant
import java.util.*

data class Passkey(
    val id: UUID,
    val userId: UUID,
    val credentialId: String,
    val publicKey: ByteArray,
    val signCount: Long,
    val deviceName: String,
    val createdAt: Instant,
    val lastUsedAt: Instant?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Passkey

        if (signCount != other.signCount) return false
        if (id != other.id) return false
        if (userId != other.userId) return false
        if (credentialId != other.credentialId) return false
        if (!publicKey.contentEquals(other.publicKey)) return false
        if (deviceName != other.deviceName) return false
        if (createdAt != other.createdAt) return false
        if (lastUsedAt != other.lastUsedAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = signCount.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + userId.hashCode()
        result = 31 * result + credentialId.hashCode()
        result = 31 * result + publicKey.contentHashCode()
        result = 31 * result + deviceName.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + (lastUsedAt?.hashCode() ?: 0)
        return result
    }
}
