package health.kokoro.api.rest.user.deletion

import health.kokoro.application.usecase.user.deletion.AbortDataDeletion
import health.kokoro.application.usecase.user.deletion.ConfirmDataDeletion
import health.kokoro.application.usecase.user.deletion.RequestDataDeletion
import health.kokoro.domain.model.user.User
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users/data-deletion")
@Validated
@Tag(name = "Data Deletion", description = "GDPR-compliant user data deletion requests")
class DataDeletionController(
    private val requestDataDeletion: RequestDataDeletion,
    private val confirmDataDeletion: ConfirmDataDeletion,
    private val abortDataDeletion: AbortDataDeletion
) {
    @PostMapping
    @Operation(summary = "Request data deletion", description = "Initiates data deletion process. Code expires in 7 days.")
    @ApiResponses(
        ApiResponse(responseCode = "202", description = "Deletion request initiated"),
        ApiResponse(responseCode = "401", description = "Unauthorized")
    )
    fun requestDataDeletion(
        @AuthenticationPrincipal user: User,
        request: HttpServletRequest
    ): ResponseEntity<Unit> {
        requestDataDeletion.execute(user, request)
        return ResponseEntity.accepted().build()
    }

    @PostMapping("/confirm")
    @Operation(summary = "Confirm data deletion", description = "Confirms deletion with verification code")
    @ApiResponses(
        ApiResponse(responseCode = "200", description = "Deletion confirmed"),
        ApiResponse(responseCode = "400", description = "Invalid or expired code")
    )
    fun confirmDataDeletion(
        @Valid @RequestBody req: DataDeletionConfirmRequestDto,
        @AuthenticationPrincipal user: User,
        request: HttpServletRequest
    ): ResponseEntity<Unit> {
        confirmDataDeletion.execute(user, req.code, request)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping
    @Operation(summary = "Abort data deletion", description = "Cancels pending deletion request")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Deletion aborted"),
        ApiResponse(responseCode = "401", description = "Unauthorized")
    )
    fun abortDataDeletion(
        @AuthenticationPrincipal user: User
    ): ResponseEntity<Unit> {
        abortDataDeletion.execute(user.id!!)
        return ResponseEntity.noContent().build()
    }
}

data class DataDeletionConfirmRequestDto(
    @field:Schema(description = "Verification code sent to email", example = "123456")
    val code: String
)
