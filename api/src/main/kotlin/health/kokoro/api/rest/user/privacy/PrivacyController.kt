package health.kokoro.api.rest.user.privacy

import health.kokoro.application.usecase.user.privacy.ExportUserData
import health.kokoro.domain.model.user.User
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users/privacy")
class PrivacyController(
    private val exportUserData: ExportUserData
) {
    @PostMapping("/data-export")
    fun requestDataExport(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<DataExportStatusResponseDto> {
        val record = exportUserData.requestExport(user)

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
