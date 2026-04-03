package health.kokoro.application.usecase.user

import health.kokoro.domain.model.file.FileType
import health.kokoro.domain.model.user.User
import health.kokoro.domain.port.file.FileRepository
import health.kokoro.domain.port.user.UserRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class UploadProfilePicture(
    private val fileUploadService: FileRepository,
    private val userRepository: UserRepository
) {
    fun execute(file: MultipartFile, user: User) {
        if (file.isEmpty) {
            throw IllegalArgumentException("Cannot upload empty file")
        }

        val file = fileUploadService.upload(
            inputStream = file.inputStream,
            filename = file.originalFilename ?: "profile-picture",
            expected = FileType.IMAGE,
            user = user
        )
        val oldPfp = user.profilePicture;
        user.profilePicture = file
        userRepository.save(user)
        oldPfp?.let { fileUploadService.delete(it.id) }
    }
}
