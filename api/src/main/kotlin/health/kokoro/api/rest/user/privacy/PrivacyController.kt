package health.kokoro.api.rest.user.privacy

import health.kokoro.application.usecase.user.privacy.ExportUserData
import health.kokoro.domain.model.user.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant
import java.util.*

@RestController
@RequestMapping("/users/privacy")
@Tag(name = "Privacy", description = "Data export and privacy")
class PrivacyController(
    private val exportUserData: ExportUserData
) {
    @PostMapping("/data-export")
    @Operation(
        summary = "Request data export",
        description = "Initiates GDPR data export as JSON attachment. Rate limited to once per 24 hours."
    )
    @ApiResponses(
        ApiResponse(responseCode = "202", description = "Export initiated"),
        ApiResponse(responseCode = "401", description = "Unauthorized"),
        ApiResponse(responseCode = "429", description = "Rate limited")
    )
    fun requestDataExport(
        @AuthenticationPrincipal user: User,
        request: HttpServletRequest
    ): ResponseEntity<DataExportStatusResponseDto> {
        val record = exportUserData.requestExport(user, request)

        return ResponseEntity.accepted().body(
            DataExportStatusResponseDto(
                exportId = record.id!!,
                status = record.status.name,
                requestedAt = record.requestedAt,
                completedAt = record.completedAt
            )
        )
    }
}

data class DataExportStatusResponseDto(
    @field:Schema(description = "Export request ID", example = "550e8400-e29b-41d4-a716-446655440000")
    val exportId: UUID,
    @field:Schema(description = "Export status", example = "PENDING")
    val status: String,
    @field:Schema(description = "Request timestamp", example = "1704067200000")
    val requestedAt: Instant,
    @field:Schema(description = "Completion timestamp", nullable = true, example = "1704153600000")
    val completedAt: Instant?
)
