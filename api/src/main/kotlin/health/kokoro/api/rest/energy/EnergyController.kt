package health.kokoro.api.rest.energy

import health.kokoro.application.usecase.energy.AddEnergyEntry
import health.kokoro.application.usecase.energy.GetEnergyEntries
import health.kokoro.application.usecase.energy.GetNextEntryAllowedDate
import health.kokoro.application.usecase.energy.GetReasons
import health.kokoro.domain.model.user.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@Validated
@RequestMapping("/energy")
@Tag(name = "Energy", description = "Daily energy tracking")
class EnergyController(
    private val getEnergyEntries: GetEnergyEntries,
    private val addEnergyEntry: AddEnergyEntry,
    private val nextEntryAllowedDate: GetNextEntryAllowedDate,
    private val getReasons: GetReasons
) {
    @GetMapping("/{date}")
    @Operation(summary = "Get energy details for specific date")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Energy details retrieved"),
        ApiResponse(responseCode = "401", description = "Unauthorized")
    )
    fun getEnergyEntriesForDay(
        @Parameter(description = "Date to query", example = "2024-01-15")
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: LocalDate,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<EnergyDetailsResponseDto> {
        val details = getEnergyEntries.getDetails(user, date)
        return ResponseEntity.ok(
            EnergyDetailsResponseDto(
                entries = details.entries.map {
                    EnergyInfoDateResponseDto(
                        date = it.date,
                        amount = it.amount,
                        reason = it.reason
                    )
                },
                influentialNegative = details.influentialNegative.let { ReasonAmountResponseDto(it.reason, it.level) },
                influentialPositive = details.influentialPositive.let { ReasonAmountResponseDto(it.reason, it.level) },
                average = details.average
            )
        )
    }

    @GetMapping
    @Operation(summary = "Get today's energy and next entry time")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Today's energy retrieved"),
        ApiResponse(responseCode = "401", description = "Unauthorized")
    )
    fun getEnergyInfoToday(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<EnergyInfoResponseDto> {
        return ResponseEntity.ok(
            EnergyInfoResponseDto(
                getEnergyEntries.getAverageToday(user),
                reason = null,
                nextEntryAllowedDate.execute(user.id!!).nextEntryAllowedAt
            )
        )
    }

    @GetMapping("/range")
    @Operation(summary = "Get energy entries for date range")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Entries retrieved"),
        ApiResponse(responseCode = "401", description = "Unauthorized")
    )
    fun getEnergyForDateRange(
        @Parameter(description = "Start date", example = "2024-01-01")
        @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) from: LocalDate,
        @Parameter(description = "End date", example = "2024-01-31")
        @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) to: LocalDate,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<List<EnergyInfoDateResponseDto>> {
        val entries = getEnergyEntries.getForDateRange(user, from, to)
            .map { EnergyInfoDateResponseDto(amount = it.amount, date = it.date, reason = it.reason) }
        return ResponseEntity.ok(entries)
    }

    @PostMapping
    @Operation(summary = "Add energy entry for today")
    @ApiResponses(
        ApiResponse(responseCode = "201", description = "Entry added"),
        ApiResponse(responseCode = "400", description = "Validation failed or rate limited"),
        ApiResponse(responseCode = "401", description = "Unauthorized")
    )
    fun addEnergyEntry(
        @Valid @RequestBody body: EnergyRequestDto,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<Any> {
        addEnergyEntry.execute(body.amount, body.reason, user)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @GetMapping("/reasons")
    @Operation(summary = "Get available energy reasons")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Reasons retrieved"),
        ApiResponse(responseCode = "401", description = "Unauthorized")
    )
    fun getEnergyReasons(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ReasonsResponseDto> {
        return ResponseEntity.ok(ReasonsResponseDto(getReasons.execute(user)))
    }
}
