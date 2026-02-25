package health.kokoro.infrastructure.jpa.user

import health.kokoro.infrastructure.jpa.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class UserEntity(
    @Column(name = "first_name", nullable = false) var firstName: String,
    @Column(name = "middle_name") var middleName: String?,
    @Column(name = "last_name", nullable = false) var lastName: String,
    @Column(name = "email", nullable = false, unique = true) var email: String,
    @Column(name = "profile_picture_url") var profilePictureUrl: String?,
    @Column(name = "password_hash", nullable = false) var passwordHash: String
) : BaseEntity()