package health.kokoro.api.rest.journal

import health.kokoro.application.usecase.journal.GetCurrentJournal
import health.kokoro.application.usecase.journal.UpdateCurrentJournal
import health.kokoro.domain.model.user.User
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/journal")
class JournalController(
    private val getCurrentJournal: GetCurrentJournal,
    private val updateCurrentJournal: UpdateCurrentJournal
) {
    @GetMapping
    fun getCurrentJournal(): ResponseEntity<JournalEntryDto> {
        val user = SecurityContextHolder.getContext().authentication.principal as User
        val journal = getCurrentJournal.execute(
            user
        )
        return ResponseEntity.ok(
            JournalEntryDto(journal.content)
        )
    }

    @PostMapping
    fun updateCurrentJournal(
        @RequestBody content: JournalRequestDto
    ): ResponseEntity<JournalEntryDto> {
        val user = SecurityContextHolder.getContext().authentication.principal as User
        val contentSaved = updateCurrentJournal.execute(user, content.content)
        return ResponseEntity.ok(
            JournalEntryDto(contentSaved)
        )
    }
}