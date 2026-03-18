package health.kokoro.domain.port.mail

interface MailSenderRepository {
    fun sendTemplate(to: String, subject: String, template: String, model: Map<String, Any>): String?
}