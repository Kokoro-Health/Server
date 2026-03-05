package health.kokoro.infrastructure.jpa.energy

import health.kokoro.infrastructure.jpa.BaseEntity
import health.kokoro.infrastructure.jpa.user.UserEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "energy_entry")
data class EnergyEntryEntity(
    @Column(name = "amount") var amount: Int,
    @JoinColumn(name = "user_id") @ManyToOne var user: UserEntity,
    @Column(name = "reason") var reason: String?
) : BaseEntity()