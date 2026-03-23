package health.kokoro.infrastructure.jpa

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant
import java.util.*

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null

    @LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: Instant = Instant.now()

    @CreatedDate
    @Column(name = "created_at")
    var createdAt: Instant = Instant.now()
}