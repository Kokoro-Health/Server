package health.kokoro.infrastructure.jpa.file

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface FileUploadJpaRepository : JpaRepository<FileUploadEntity, UUID>