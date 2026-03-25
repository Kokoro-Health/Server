package health.kokoro.application.usecase.journal

import health.kokoro.domain.model.user.User
import health.kokoro.domain.port.journal.JournalRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class UpdateCurrentJournal(
    private val repo: JournalRepository,
) {
    fun execute(user: User, id: UUID?, content: String): Response {
        return repo.save(user.id!!, id, content).let { Response(id = it.id, it.content, it.availableUntil) }
    }

    data class Response(val id: UUID?, val content: String, val availableUntil: Instant?)
}