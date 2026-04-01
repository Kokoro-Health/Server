package health.kokoro.domain.port.file

import health.kokoro.domain.model.file.FileType
import health.kokoro.domain.model.file.FileUpload
import health.kokoro.domain.model.user.User
import java.io.InputStream
import java.util.*

interface FileRepository {
    fun upload(inputStream: InputStream, filename: String, expected: FileType, user: User): FileUpload
    fun delete(id: UUID)
    fun getById(id: UUID): FileUpload
}
