package health.kokoro.infrastructure.jpa.file

import health.kokoro.domain.model.file.FileType
import health.kokoro.infrastructure.jpa.BaseEntity
import health.kokoro.infrastructure.jpa.user.UserEntity
import jakarta.persistence.*

@Entity
@Table(name = "file_uploads")
class FileUploadEntity(
    @Column(name = "name") val name: String,
    @Column(name = "type") @Enumerated(EnumType.STRING) val type: FileType,
    @JoinColumn(name = "user_id") @ManyToOne val uploadedBy: UserEntity,
    @Column(name = "uri") val uri: String
) : BaseEntity()