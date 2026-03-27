package health.kokoro.application.usecase.journal

import health.kokoro.domain.model.journal.JournalEntry
import health.kokoro.domain.port.journal.JournalRepository
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class GetJournals(
    private val repo: JournalRepository,
) {
    fun getById(id: UUID, userId: UUID): Response {
        validateOwner(id, userId)
        val entry = repo.getById(id) ?: throw IllegalStateException("")
        return toResponse(entry)
    }

    fun getAll(userId: UUID): List<Response> {
        return repo.getAllByUserId(userId).filter { it.lockedAt.isBefore(Instant.now()) }.map { toResponse(it) }
    }

    private fun validateOwner(id: UUID, userID: UUID) {
        val entry = repo.getById(id)
        if (entry?.userId != userID) {
            throw AccessDeniedException("This entry belongs to a different user.")
        }
    }

    private fun toResponse(entry: JournalEntry): Response {
        val shortenContent = entry.content.length > 64
        var shortContent = entry.content
        if (shortenContent) {
            shortContent = entry.content.substring(0, 64)
        }
        return Response(
            id = entry.id!!,
            content = entry.content,
            shortContent = shortContent,
            createdAt = entry.createdAt,
            updatedAt = entry.updatedAt,
            lockedAt = entry.lockedAt
        )
    }

    data class Response(
        val content: String,
        val shortContent: String,
        val id: UUID,
        val createdAt: Instant,
        val updatedAt: Instant,
        val lockedAt: Instant
    )
}