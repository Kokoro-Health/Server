package health.kokoro.application.usecase.journal

import health.kokoro.domain.model.user.User
import health.kokoro.domain.port.journal.JournalRepository
import org.springframework.stereotype.Service

@Service
class GetCurrentJournal(
    private val repo: JournalRepository,
) {
    fun execute(user: User): Response {
        return repo.getCurrentJournal(user.id!!)?.let { Response(content = it.content) } ?: Response(content = "")
    }

    data class Response(
        val content: String,
    )
}