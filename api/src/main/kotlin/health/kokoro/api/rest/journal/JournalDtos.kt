package health.kokoro.api.rest.journal

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant
import java.util.*

data class JournalEntryResponseDto(
    @field:Schema(description = "Journal entry ID", nullable = true)
    val id: UUID?,
    @field:Schema(description = "Journal content (markdown supported)")
    val content: String,
    @field:Schema(description = "When entry becomes locked for editing", nullable = true, example = "1704153600000")
    val availableUntil: Instant?
)

data class JournalRequestDto(
    @field:Schema(description = "Journal content", example = "Today was a good day...")
    val content: String
)

data class ShortJournalResponseDto(
    @field:Schema(description = "Journal entry ID")
    val id: UUID,
    @field:Schema(description = "First 100 characters of content")
    val content: String,
    @field:Schema(description = "When entry was locked")
    val lockedSince: Instant
)
