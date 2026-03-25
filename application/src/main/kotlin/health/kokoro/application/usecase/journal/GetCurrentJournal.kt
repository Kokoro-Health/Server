package health.kokoro.application.usecase.journal

import health.kokoro.domain.model.user.User
import health.kokoro.domain.port.journal.JournalRepository
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class GetCurrentJournal(
    private val repo: JournalRepository,
) {
    fun execute(user: User): Response {
        val current = repo.getCurrentJournal(user.id!!)
        if (current?.content.isNullOrBlank()) {
            return Response(content = "", availableUntil = null, id = null)
        }
        return current.let { Response(content = it.content, availableUntil = it.availableUntil, id = it.id) }
    }

    data class Response(
        val id: UUID?,
        val content: String,
        val availableUntil: Instant?
    )
}