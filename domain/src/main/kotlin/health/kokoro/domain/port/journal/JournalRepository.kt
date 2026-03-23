package health.kokoro.domain.port.journal

import health.kokoro.domain.model.journal.JournalEntry
import java.util.*

interface JournalRepository {
    fun getCurrentJournal(userId: UUID): JournalEntry?
    fun save(userId: UUID, content: String): JournalEntry
}