package health.kokoro.infrastructure.jpa.journal

import health.kokoro.domain.model.security.EncryptedData
import health.kokoro.infrastructure.converter.EncryptedDataConverter
import health.kokoro.infrastructure.jpa.BaseEntity
import health.kokoro.infrastructure.jpa.user.UserEntity
import jakarta.persistence.*

@Entity
@Table(name = "journal_entries")
data class JournalEntryEntity(
    @Convert(converter = EncryptedDataConverter::class) @Column("content", length = 1028) var content: EncryptedData,
    @JoinColumn(name = "user_id") @ManyToOne val user: UserEntity
) : BaseEntity()