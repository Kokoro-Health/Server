package health.kokoro.api.rest.energy

import health.kokoro.application.usecase.energy.AddEnergyEntry
import health.kokoro.application.usecase.energy.GetEnergyEntries
import health.kokoro.application.usecase.energy.GetNextEntryAllowedDate
import health.kokoro.application.usecase.energy.GetReasons
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
    private val getEnergyEntries: GetEnergyEntries,
    private val addEnergyEntry: AddEnergyEntry,
    private val nextEntryAllowedDate: GetNextEntryAllowedDate,
    private val getReasons: GetReasons
) {
    @GetMapping("/{date}")
    fun getEnergyEntriesForDay(
        @PathVariable date: Instant
    ): ResponseEntity<EnergyDetailsDto> {
        val user = SecurityContextHolder.getContext().authentication.principal as User
        val details = getEnergyEntries.getDetails(user, date)
        return ResponseEntity.ok(
            EnergyDetailsDto(
                entries = details.entries.map {
                    EnergyInfoDateDto(
                        date = it.date,
                        amount = it.amount,
                        reason = it.reason
                    )
                },
                influentialNegative = details.influentialNegative.let { ReasonAmount(it.reason, it.level) },
                influentialPositive = details.influentialPositive.let { ReasonAmount(it.reason, it.level) },
                average = details.average
            )
        )
    }

    @GetMapping
    fun getEnergyInfoToday(): ResponseEntity<EnergyInfoDto> {
        val user = SecurityContextHolder.getContext().authentication.principal as User
        return ResponseEntity.ok(
            EnergyInfoDto(
                getEnergyEntries.getAverageToday(user),
                reason = null,
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
        val entries = getEnergyEntries.getForDateRange(user, from, to)
            .map { EnergyInfoDateDto(amount = it.amount, date = it.date, reason = it.reason) }
        return ResponseEntity.ok(entries)
    }

    @PostMapping("/add")
    fun addEnergyEntry(@RequestBody @Valid body: EnergyRequestDto): ResponseEntity<Any> {
        val user = SecurityContextHolder.getContext().authentication.principal as User
        addEnergyEntry.execute(body.amount, body.reason, user)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/reasons")
    fun getEnergyReasons(): ResponseEntity<ReasonsResponseDto> {
        val user = SecurityContextHolder.getContext().authentication.principal as User
        return ResponseEntity.ok(ReasonsResponseDto(getReasons.execute(user)))
    }
}