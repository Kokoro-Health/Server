package health.kokoro.infrastructure.adapter.file

import health.kokoro.domain.model.file.FileType
import health.kokoro.domain.model.file.FileUpload
import health.kokoro.domain.model.user.User
import health.kokoro.domain.port.file.FileRepository
import health.kokoro.infrastructure.config.FileStorageConfig
import health.kokoro.infrastructure.jpa.file.FileUploadEntity
import health.kokoro.infrastructure.jpa.file.FileUploadJpaRepository
import health.kokoro.infrastructure.jpa.user.UserJpaRepository
import health.kokoro.infrastructure.util.file.FileTypeValidator
import org.springframework.stereotype.Repository
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.*

@Repository
class FileRepositoryAdapter(
    private val config: FileStorageConfig,
    private val jpa: FileUploadJpaRepository,
    private val validator: FileTypeValidator,
    private val userJpa: UserJpaRepository,
) : FileRepository {

    override fun upload(inputStream: InputStream, filename: String, expected: FileType, user: User): FileUpload {
        val userEntity = userJpa.findById(user.id!!).orElseThrow()

        val parentDir = getParentDir(expected)
        val fileExtension = getFileExtension(filename)
        val fileName = "${UUID.randomUUID()}$fileExtension"
        val targetFile = File(parentDir, fileName)

        inputStream.use { input ->
            Files.copy(input, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }

        validator.validate(targetFile, expected)
        val strippedFileName = filename.substringBeforeLast(".")

        val entity = jpa.save(
            FileUploadEntity(
                type = expected,
                uploadedBy = userEntity,
                uri = targetFile.absolutePath,
                name = strippedFileName
            )
        )


        return FileUpload(
            id = entity.id!!,
            createdAt = entity.createdAt,
            type = expected,
            uploadedByUserId = user.id!!,
            content = Files.readAllBytes(targetFile.toPath()),
            name = strippedFileName,
            pathName = entity.uri
        )
    }


    override fun delete(id: UUID) {
        jpa.findById(id).ifPresentOrElse(
            { entity ->
                File(entity.uri).delete()
                jpa.delete(entity)
            },
            { throw NoSuchElementException("File with id $id not found") }
        )
    }

    override fun getById(id: UUID): FileUpload {
        val entity = jpa.findById(id)
            .orElseThrow { NoSuchElementException("File with id $id not found") }

        return FileUpload(
            id = entity.id!!,
            createdAt = entity.createdAt,
            type = entity.type,
            uploadedByUserId = entity.uploadedBy.id!!,
            content = File(entity.uri).readBytes(),
            name = entity.name,
            pathName = entity.uri
        )
    }

    private fun getParentDir(type: FileType): File {
        val typeDir = File(getParentDir(), type.name.lowercase())
        if (!typeDir.exists()) {
            typeDir.mkdirs()
        }
        return typeDir
    }

    private fun getParentDir(): File {
        val parent = File(config.path)
        if (!parent.exists()) {
            parent.mkdirs()
        }
        return parent
    }

    private fun getFileExtension(filename: String): String {
        return filename.substringAfterLast('.', "")
            .takeIf { it.isNotEmpty() }
            ?.let { ".$it" }
            ?: ""
    }
}
