package health.kokoro.infrastructure.adapter

import health.kokoro.domain.port.mail.MailSenderRepository
import health.kokoro.infrastructure.config.MailConfig
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Repository
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

@Repository
class MailSenderRepositoryAdapter(
    private val mailSender: JavaMailSender,
    private val config: MailConfig,
    private val templateEngine: TemplateEngine,
) : MailSenderRepository {
    override fun sendTemplate(
        to: String,
        subject: String,
        template: String,
        model: Map<String, Any>
    ): String? {
        val context = Context().apply {
            setVariables(model)
        }

        val htmlContent = templateEngine.process("mail/$template", context)

        val mimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(mimeMessage, true, "UTF-8")

        helper.setTo(to)
        helper.setFrom(config.username, "Kokoro")
        helper.setSubject(subject)
        helper.setText(htmlContent, true)

        mailSender.send(mimeMessage)

        return null
    }
}