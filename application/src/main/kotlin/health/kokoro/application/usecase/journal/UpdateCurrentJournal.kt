package health.kokoro.application.usecase.journal

import health.kokoro.domain.model.user.User
import health.kokoro.domain.port.journal.JournalRepository
import org.springframework.stereotype.Service

@Service
class UpdateCurrentJournal(
    private val repo: JournalRepository,
) {
    fun execute(user: User, content: String): String {
        return repo.save(user.id!!, content).content
    }
}