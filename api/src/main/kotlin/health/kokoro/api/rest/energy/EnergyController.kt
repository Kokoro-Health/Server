package health.kokoro.api.rest.energy

import health.kokoro.application.usecase.energy.AddEnergyEntry
import health.kokoro.application.usecase.energy.GetEnergyPercentage
import health.kokoro.application.usecase.energy.GetNextEntryAllowedDate
import health.kokoro.domain.model.user.User
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.time.Instant

@RestController
@Validated
@RequestMapping("/energy")
class EnergyController(
    private val getEnergyAmount: GetEnergyPercentage,
    private val addEnergyEntry: AddEnergyEntry,
    private val nextEntryAllowedDate: GetNextEntryAllowedDate
) {
    @GetMapping
    fun getEnergyInfo(): ResponseEntity<EnergyInfoDto> {
        val user = SecurityContextHolder.getContext().authentication.principal as User
        return ResponseEntity.ok(
            EnergyInfoDto(
                getEnergyAmount.execute(user.id!!),
                nextEntryAllowedDate.execute(user.id!!).nextEntryAllowedAt
            )
        )
    }

    @GetMapping("/recent")
    fun getEnergyForDateRange(
        @RequestParam("from") from: Instant,
        @RequestParam("to") to: Instant,
    ): ResponseEntity<List<EnergyInfoDateDto>> {
        val user = SecurityContextHolder.getContext().authentication.principal as User
        val entries = getEnergyAmount.executeInDateRange(user.id!!, from, to)
            .map { EnergyInfoDateDto(amount = it.amount, date = it.date) }
        return ResponseEntity.ok(entries)
    }

    @PostMapping("/add")
    fun addEnergyEntry(@RequestBody @Valid body: EnergyRequestDto): ResponseEntity<Any> {
        val user = SecurityContextHolder.getContext().authentication.principal as User
        addEnergyEntry.execute(body.amount, user)
        return ResponseEntity.ok().build()
    }
}