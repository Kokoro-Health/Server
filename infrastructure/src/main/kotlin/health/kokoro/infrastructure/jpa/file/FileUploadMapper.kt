package health.kokoro.infrastructure.jpa.file

import health.kokoro.domain.model.file.FileUpload
import health.kokoro.domain.port.file.FileRepository
import health.kokoro.infrastructure.jpa.user.UserJpaRepository
import org.springframework.stereotype.Component

@Component
class FileUploadMapper(
    private val userJpa: UserJpaRepository,
    private val fileRepo: FileRepository
) {
    fun toDomain(ent: FileUploadEntity): FileUpload {
        return FileUpload(
            id = ent.id!!,
            createdAt = ent.createdAt,
            type = ent.type,
            uploadedByUserId = ent.uploadedBy.id!!,
            content = fileRepo.getById(ent.id!!).content,
            name = ent.name,
            pathName = ent.uri
        )
    }

    fun toEntity(domain: FileUpload): FileUploadEntity {
        val user = userJpa.findById(domain.uploadedByUserId).orElseThrow()
    return FileUploadEntity(
        name = domain.name,
        type = domain.type,
        uploadedBy =user,
        uri = domain.pathName
    )
    }
}