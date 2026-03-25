package health.kokoro.api.rest.journal

import io.swagger.v3.oas.annotations.media.Schema
import java.time.Instant
import java.util.*

data class JournalEntryDto(
    @field:Schema(nullable = true) val id: UUID?,
    val content: String,
    @field:Schema(nullable = true) val availableUntil: Instant?
)

data class JournalRequestDto(
    val content: String
)

data class ShortJournalResponseDto(
    val id: UUID,
    val content: String,
    val lockedSince: Instant
)