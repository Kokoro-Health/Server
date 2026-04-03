package health.kokoro.infrastructure.util.file

import health.kokoro.domain.model.file.FileType
import org.apache.tika.Tika
import org.apache.tika.mime.MimeTypeException
import org.springframework.stereotype.Component
import java.io.File
import java.io.IOException

@Component
class FileTypeValidator {
    private val tika = Tika()

    fun validate(file: File, expected: FileType) {
        require(file.exists()) { "File does not exist" }
        require(file.isFile) { "Path does not point to a file" }

        try {
            val detectedMimeType = tika.detect(file)

            val fileExtension = getFileExtension(file)
                ?: throw IllegalArgumentException("File has no extension")

            val allowedExtensions = when (expected) {
                FileType.IMAGE -> {
                    when (detectedMimeType) {
                        "image/png" -> listOf("png")
                        "image/jpeg" -> listOf("jpg", "jpeg")
                        "image/svg+xml" -> listOf("svg")
                        "image/webp" -> listOf("webp")
                        else -> throw SecurityException("Unsupported image type: $detectedMimeType")
                    }
                }
            }

            if (!allowedExtensions.contains(fileExtension.lowercase())) {
                throw SecurityException("File extension '$fileExtension' does not match detected content type '$detectedMimeType'")
            }

        } catch (e: IOException) {
            throw SecurityException("Failed to read file content", e)
        } catch (e: MimeTypeException) {
            throw SecurityException("Failed to determine file type", e)
        }
    }

    private fun getFileExtension(file: File): String? {
        val fileName = file.name
        val lastDotIndex = fileName.lastIndexOf('.')
        return if (lastDotIndex > 0 && lastDotIndex < fileName.length - 1) {
            fileName.substring(lastDotIndex + 1)
        } else {
            null
        }
    }
}
