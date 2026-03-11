package health.kokoro.infrastructure.jpa.energy

import health.kokoro.infrastructure.jpa.BaseEntity
import health.kokoro.infrastructure.jpa.user.UserEntity
import jakarta.persistence.*

@Entity
@Table(name = "energy_entry")
data class EnergyEntryEntity(
    @Column(name = "amount") var amount: Int,
    @JoinColumn(name = "user_id") @ManyToOne var user: UserEntity,
    @Column(name = "reason") var reason: String?
) : BaseEntity()