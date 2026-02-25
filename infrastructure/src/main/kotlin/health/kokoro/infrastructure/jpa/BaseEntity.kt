package health.kokoro.infrastructure.jpa

import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.Instant
import java.util.*

@MappedSuperclass
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