import android.net.Uri
import java.io.File
import java.util.*
import javax.mail.*
import javax.mail.internet.*

class EmailSender {

    companion object {
        private const val EMAIL = "dmxp72@gmail.com" // Твой email
        private const val PASSWORD = "Dimap2634!" // Пароль от email
        private const val HOST = "smtp.gmail.com" // SMTP-хост ля Gmail
        private const val PORT = "465" // Порт для SMTP
    }

    fun sendEmail(subject: String, body: String, attachments: List<Uri>) {
        val props = Properties().apply {
            put("mail.smtp.host", HOST)
            put("mail.smtp.port", PORT)
            put("mail.smtp.auth", "true")
            put("mail.smtp.socketFactory.port", PORT)
            put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
        }

        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication(): PasswordAuthentication {
                return PasswordAuthentication(EMAIL, PASSWORD)
            }
        })

        try {
            val message = MimeMessage(session).apply {
                setFrom(InternetAddress(EMAIL))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse("d.palaumov@yandex.ru"))
                setSubject(subject)
                setText(body)

                val multipart = MimeMultipart()
                val messageBodyPart = MimeBodyPart()
                messageBodyPart.setText(body)
                multipart.addBodyPart(messageBodyPart)

                attachments.forEach { uri ->
                    val attachmentBodyPart = MimeBodyPart()
                    attachmentBodyPart.attachFile(File(uri.path))
                    multipart.addBodyPart(attachmentBodyPart)
                }

                setContent(multipart)
            }

            Transport.send(message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}