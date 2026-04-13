package health.kokoro.infrastructure.adapter.mail

import health.kokoro.domain.port.mail.MailSenderRepository
import health.kokoro.infrastructure.config.MailConfig
import jakarta.mail.internet.MimeMessage
import jakarta.mail.util.ByteArrayDataSource
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
    ) {
        val mimeMessage = mailSender.createMimeMessage()

        getHelper(template, model, to, subject, mimeMessage)

        mailSender.send(mimeMessage)
    }

    override fun sendTemplateWithAttachment(
        to: String,
        subject: String,
        template: String,
        model: Map<String, Any>,
        attachmentName: String,
        attachmentData: ByteArray,
        attachmentMimeType: String
    ) {

        val mimeMessage = mailSender.createMimeMessage()
        val helper = getHelper(template, model, to, subject, mimeMessage)

        helper.addAttachment(attachmentName, ByteArrayDataSource(attachmentData, attachmentMimeType))


        mailSender.send(mimeMessage)
    }

    fun getHelper(
        template: String,
        model: Map<String, Any>,
        to: String,
        subject: String,
        mimeMessage: MimeMessage
    ): MimeMessageHelper {
        val context = Context().apply {
            setVariables(model)
        }

        val htmlContent = templateEngine.process("mail/$template", context)

        val helper = MimeMessageHelper(mimeMessage, true, "UTF-8")

        helper.setTo(to)
        helper.setFrom(config.username, "Kokoro")
        helper.setSubject(subject)
        helper.setText(htmlContent, true)

        return helper
    }
}