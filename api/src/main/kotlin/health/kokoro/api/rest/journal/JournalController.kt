package health.kokoro.api.rest.journal

import health.kokoro.application.usecase.journal.GetCurrentJournal
import health.kokoro.application.usecase.journal.GetJournals
import health.kokoro.application.usecase.journal.UpdateCurrentJournal
import health.kokoro.domain.model.user.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/journals")
@Tag(name = "Journal", description = "Daily journal entries")
class JournalController(
    private val getCurrentJournal: GetCurrentJournal,
    private val updateCurrentJournal: UpdateCurrentJournal,
    private val getJournals: GetJournals
) {
    @GetMapping
    @Operation(summary = "Get today's journal entry")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Journal entry retrieved"),
        ApiResponse(responseCode = "401", description = "Unauthorized")
    )
    fun getCurrentJournal(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<JournalEntryResponseDto> {
        val journal = getCurrentJournal.execute(user)
        return ResponseEntity.ok(
            JournalEntryResponseDto(content = journal.content, availableUntil = journal.availableUntil, id = journal.id)
        )
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update journal entry", description = "ID is ignored - always updates today's entry")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Entry updated"),
        ApiResponse(responseCode = "400", description = "Entry is locked"),
        ApiResponse(responseCode = "401", description = "Unauthorized")
    )
    fun updateCurrentJournal(
        @RequestBody content: JournalRequestDto,
        @PathVariable @Schema(nullable = true) id: UUID?,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<JournalEntryResponseDto> {
        val contentSaved = updateCurrentJournal.execute(user, id, content.content)
        return ResponseEntity.ok(
            JournalEntryResponseDto(id = contentSaved.id, content = content.content, availableUntil = contentSaved.availableUntil)
        )
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get journal entry by ID")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Journal entry retrieved"),
        ApiResponse(responseCode = "404", description = "Entry not found")
    )
    fun getJournalById(
        @Parameter(description = "Journal entry ID") @PathVariable id: UUID,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<JournalEntryResponseDto> {
        val journal = getJournals.getById(id, user.id!!)
        return ResponseEntity.ok(
            JournalEntryResponseDto(
                id = journal.id,
                content = journal.content,
                availableUntil = journal.lockedAt
            )
        )
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recent journal entries")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Entries retrieved"),
        ApiResponse(responseCode = "401", description = "Unauthorized")
    )
    fun getRecentJournalsShort(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<List<ShortJournalResponseDto>> {
        val recents = getJournals.getAll(user.id!!)

        return ResponseEntity.ok(recents.map {
            ShortJournalResponseDto(
                id = it.id,
                content = it.shortContent,
                lockedSince = it.lockedAt
            )
        })
    }
}
