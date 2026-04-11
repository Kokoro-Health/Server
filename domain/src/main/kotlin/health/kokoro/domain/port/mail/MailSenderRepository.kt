package health.kokoro.domain.port.mail

interface MailSenderRepository {
    fun sendTemplate(to: String, subject: String, template: String, model: Map<String, Any>)
    fun sendTemplateWithAttachment(
        to: String,
        subject: String,
        template: String,
        model: Map<String, Any>,
        attachmentName: String,
        attachmentData: ByteArray,
        attachmentMimeType: String = "application/octet-stream"
    )
}