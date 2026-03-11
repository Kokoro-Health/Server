package health.kokoro.api.rest.energy

import health.kokoro.application.usecase.energy.AddEnergyEntry
import health.kokoro.application.usecase.energy.GetEnergyPercentage
import health.kokoro.application.usecase.energy.GetNextEntryAllowedDate
import health.kokoro.domain.model.user.User
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Validated
@RequestMapping("/energy")
class EnergyController(
    private val getEnergyAmount: GetEnergyPercentage,
    private val addEnergyEntry: AddEnergyEntry,
    private val nextEntryAllowedDate: GetNextEntryAllowedDate
){
    @GetMapping
    fun getEnergyInfo(): ResponseEntity<EnergyInfoDto> {
       val user = SecurityContextHolder.getContext().authentication.principal as User
       return ResponseEntity.ok(
           EnergyInfoDto(getEnergyAmount.execute(user.id!!), nextEntryAllowedDate.execute(user.id!!).nextEntryAllowedAt)
       )
    }

    @PostMapping("/add")
    fun addEnergyEntry(@RequestBody @Valid body: EnergyRequestDto): ResponseEntity<Any> {
        val user = SecurityContextHolder.getContext().authentication.principal as User
        addEnergyEntry.execute(body.amount, user)
        return ResponseEntity.ok().build()
    }
}