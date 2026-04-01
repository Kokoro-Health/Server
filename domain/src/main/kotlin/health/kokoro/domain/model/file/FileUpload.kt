package health.kokoro.domain.model.file

import java.time.Instant
import java.util.UUID

data class FileUpload(
    val id: UUID,
    val name: String,
    val createdAt: Instant,
    val type: FileType,
    val uploadedByUserId: UUID,
    val content: ByteArray,
    val pathName: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FileUpload

        if (id != other.id) return false
        if (name != other.name) return false
        if (createdAt != other.createdAt) return false
        if (type != other.type) return false
        if (uploadedByUserId != other.uploadedByUserId) return false
        if (!content.contentEquals(other.content)) return false
        if (pathName != other.pathName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + uploadedByUserId.hashCode()
        result = 31 * result + content.contentHashCode()
        result = 31 * result + pathName.hashCode()
        return result
    }
}