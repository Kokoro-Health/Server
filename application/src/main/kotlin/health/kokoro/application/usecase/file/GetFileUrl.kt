package health.kokoro.application.usecase.file

import health.kokoro.domain.model.file.FileType
import health.kokoro.domain.model.file.FileUpload
import org.springframework.stereotype.Service
import java.util.*

@Service
class GetFileUrl {
    fun execute(upload: FileUpload): String {
        if (upload.type == FileType.IMAGE) {
            val extension = upload.pathName.substringAfterLast('.').lowercase()
            val mimeType = when (extension) {
                "svg" -> "image/svg+xml"
                "png" -> "image/png"
                "jpg", "jpeg" -> "image/jpeg"
                "webp" -> "image/webp"
                else -> throw IllegalArgumentException("Unsupported image extension: $extension")
            }

            return if (extension == "svg") {
                "data:image/svg+xml;utf8,${String(upload.content, Charsets.UTF_8)}"
            } else {
                "data:$mimeType;base64,${Base64.getEncoder().encodeToString(upload.content)}"
            }
        }
        throw IllegalArgumentException("Unsupported file type ${upload.type} (Extensions: ${upload.type.extensions})")
    }
}
