package health.kokoro.infrastructure.jpa.file

import health.kokoro.domain.model.file.FileType
import health.kokoro.infrastructure.jpa.BaseEntity
import health.kokoro.infrastructure.jpa.user.UserEntity
import jakarta.persistence.*

@Entity
@Table(name = "file_uploads")
class FileUploadEntity(
    @Column(name = "name") var name: String,
    @Column(name = "type") @Enumerated(EnumType.STRING) var type: FileType,
    @JoinColumn(name = "user_id") @ManyToOne var uploadedBy: UserEntity,
    @Column(name = "uri") var uri: String
) : BaseEntity()