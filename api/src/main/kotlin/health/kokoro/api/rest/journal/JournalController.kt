package health.kokoro.api.rest.journal

import health.kokoro.application.usecase.journal.GetCurrentJournal
import health.kokoro.application.usecase.journal.GetJournals
import health.kokoro.application.usecase.journal.UpdateCurrentJournal
import health.kokoro.domain.model.user.User
import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/journals")
class JournalController(
    private val getCurrentJournal: GetCurrentJournal,
    private val updateCurrentJournal: UpdateCurrentJournal,
    private val getJournals: GetJournals
) {
    @GetMapping
    fun getCurrentJournal(): ResponseEntity<JournalEntryDto> {
        val user = SecurityContextHolder.getContext().authentication.principal as User
        val journal = getCurrentJournal.execute(
            user
        )
        return ResponseEntity.ok(
            JournalEntryDto(content = journal.content, availableUntil = journal.availableUntil, id = journal.id)
        )
    }

    @PostMapping("/{id}")
    fun updateCurrentJournal(
        @RequestBody content: JournalRequestDto,
        @PathVariable @Schema(nullable = true) id: UUID?
    ): ResponseEntity<JournalEntryDto> {
        val user = SecurityContextHolder.getContext().authentication.principal as User
        val contentSaved = updateCurrentJournal.execute(user, id, content.content)
        return ResponseEntity.ok(
            JournalEntryDto(id = contentSaved.id, content.content, contentSaved.availableUntil)
        )
    }

    @PostMapping
    fun updateCurrentJournal(
        @RequestBody content: JournalRequestDto,
    ): ResponseEntity<JournalEntryDto> {
        val user = SecurityContextHolder.getContext().authentication.principal as User
        val contentSaved = updateCurrentJournal.execute(user, null, content.content)
        return ResponseEntity.ok(
            JournalEntryDto(id = contentSaved.id, content.content, contentSaved.availableUntil)
        )
    }

    @GetMapping("/{id}")
    fun getJournalById(
        @PathVariable id: UUID
    ): ResponseEntity<JournalEntryDto> {
        val user = SecurityContextHolder.getContext().authentication.principal as User
        val journal = getJournals.getById(id, user.id!!)
        return ResponseEntity.ok(
            JournalEntryDto(
                id = journal.id,
                content = journal.content,
                availableUntil = journal.lockedAt
            )
        )
    }

    @GetMapping("/recent")
    fun getRecentJournalsShort(): ResponseEntity<List<ShortJournalResponseDto>> {
        val user = SecurityContextHolder.getContext().authentication.principal as User
        val recents = getJournals.getAll(user.id!!)

        return ResponseEntity.ok(recents.map {
            ShortJournalResponseDto(
                it.id,
                content = it.shortContent,
                lockedSince = it.lockedAt
            )
        })
    }
}