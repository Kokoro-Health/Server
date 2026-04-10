package health.kokoro.domain.model.audit

enum class AuditAction {
    LOGIN_SUCCESS,
    LOGIN_FAILED,
    PASSWORD_CHANGE,
    MFA_ENABLED,
    MFA_DISABLED,
    DATA_EXPORT,
    DATA_DELETION_REQUEST,
    DATA_DELETION_CONFIRMED
}