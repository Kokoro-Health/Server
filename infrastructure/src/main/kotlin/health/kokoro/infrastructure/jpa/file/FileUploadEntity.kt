package health.kokoro.infrastructure.jpa.file

import health.kokoro.domain.model.file.FileType
import health.kokoro.infrastructure.jpa.BaseEntity
import health.kokoro.infrastructure.jpa.user.UserEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "file_upload")
class FileUploadEntity(
    @Column(name = "name") val name: String,
    @Column(name = "type") @Enumerated(EnumType.STRING) val type: FileType,
    @JoinColumn(name = "user_id") @ManyToOne val uploadedBy: UserEntity,
    @Column(name = "uri") val uri: String
): BaseEntity()