package health.kokoro.domain.port.journal

import health.kokoro.domain.model.journal.JournalEntry
import java.util.*

interface JournalRepository {
    fun getCurrentJournal(userId: UUID): JournalEntry?
    fun save(userId: UUID, id: UUID?, content: String): JournalEntry
    fun getById(uuid: UUID): JournalEntry?
    fun getAllByUserId(userId: UUID): List<JournalEntry>
}