import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.myapplication.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.activation.DataHandler
import javax.activation.DataSource
import javax.activation.FileDataSource
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

class ReporterFragment : Fragment() {

    private lateinit var editTextName: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var buttonAttachPhoto: Button
    private lateinit var buttonAttachVideo: Button
    private lateinit var buttonSend: Button
    private lateinit var layoutAttachedFiles: LinearLayout
    private lateinit var progressBar: ProgressBar

    private val attachedFiles = mutableListOf<Uri>()

    // Конфигурация почты
    companion object {
        private const val PICK_IMAGE_REQUEST = 101
        private const val PICK_VIDEO_REQUEST = 102
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reporter, container, false)

        editTextName = view.findViewById(R.id.editTextName)
        editTextDescription = view.findViewById(R.id.editTextDescription)
        buttonAttachPhoto = view.findViewById(R.id.buttonAttachPhoto)
        buttonAttachVideo = view.findViewById(R.id.buttonAttachVideo)
        buttonSend = view.findViewById(R.id.buttonSend)
        layoutAttachedFiles = view.findViewById(R.id.layoutAttachedFiles)
        progressBar = view.findViewById(R.id.progressBar)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonAttachPhoto.setOnClickListener {
            openFilePicker("image/*")
        }

        buttonAttachVideo.setOnClickListener {
            openFilePicker("video/*")
        }

        buttonSend.setOnClickListener {
            sendEmail()
        }
    }

    private fun openFilePicker(mimeType: String) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = mimeType
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(intent, when (mimeType) {
            "image/*" -> PICK_IMAGE_REQUEST
            "video/*" -> PICK_VIDEO_REQUEST
            else -> PICK_IMAGE_REQUEST
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == android.app.Activity.RESULT_OK && data != null) {
            val uri = data.data
            if (uri != null) {
                attachedFiles.add(uri)
                updateAttachedFilesList()
            }
        }
    }
    private fun updateAttachedFilesList() {
        layoutAttachedFiles.removeAllViews()

        attachedFiles.forEachIndexed { index, uri ->
            val view = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_attachment, layoutAttachedFiles, false)

            val imagePreview = view.findViewById<ImageView>(R.id.imagePreview)
            val videoPreview = view.findViewById<VideoView>(R.id.videoPreview)
            val tvFileType = view.findViewById<TextView>(R.id.tvFileType)
            val btnRemove = view.findViewById<ImageButton>(R.id.btnRemove)

            // Определяем тип файла
            val mimeType = requireContext().contentResolver.getType(uri) ?: ""
            when {
                mimeType.startsWith("image/") -> {
                    imagePreview.visibility = View.VISIBLE
                    Glide.with(requireContext())
                        .load(uri)
                        .into(imagePreview)
                    tvFileType.text = "Фото"
                }
                mimeType.startsWith("video/") -> {
                    videoPreview.visibility = View.VISIBLE
                    videoPreview.setVideoURI(uri)
                    videoPreview.seekTo(1000) // Показываем не первый кадр
                    tvFileType.text = "Видео"
                }
                else -> {
                    tvFileType.text = "Файл"
                }
            }

            btnRemove.setOnClickListener {
                attachedFiles.remove(uri)
                updateAttachedFilesList()
            }

            layoutAttachedFiles.addView(view)
        }
    }

    private fun getFileNameFromUri(uri: Uri): String {
        val cursor = requireActivity().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayName = it.getString(it.getColumnIndexOrThrow("_display_name"))
                if (displayName != null) return displayName
            }
        }
        return uri.path?.substringAfterLast('/') ?: "file"
    }
    private fun sendEmail() {
        val name = editTextName.text.toString().trim()
        val description = editTextDescription.text.toString().trim()

        // Валидация полей
        when {
            name.isEmpty() -> {
                editTextName.error = "Введите ваше имя"
                return
            }
            description.isEmpty() -> {
                editTextDescription.error = "Введите описание события"
                return
            }
        }

        // Блокируем UI на время отправки
        progressBar.visibility = View.VISIBLE
        buttonSend.isEnabled = false
        buttonAttachPhoto.isEnabled = false
        buttonAttachVideo.isEnabled = false

        val emailTask = object : AsyncTask<Void, Void, Pair<Boolean, String>>() {
            override fun doInBackground(vararg params: Void?): Pair<Boolean, String> {
                return try {
                    // Конфигурация SMTP
                    val props = Properties().apply {
                        put("mail.smtp.host", "smtp.yandex.ru")
                        put("mail.smtp.port", "465")
                        put("mail.smtp.auth", "true")
                        put("mail.smtp.ssl.enable", "true")
                        put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory")
                        put("mail.smtp.connectiontimeout", "15000") // 15 секунд таймаут
                        put("mail.smtp.timeout", "15000")
                    }

                    // Создаем сессию с аутентификацией
                    val session = Session.getInstance(props, object : Authenticator() {
                        override fun getPasswordAuthentication() = PasswordAuthentication(
                            "androidtestVKR@yandex.ru",
                            "eocyeyspqavnldlc"
                        )
                    }).apply {
                        setDebug(true) // Включить логирование (для отладки)
                    }

                    // Формируем сообщение
                    val message = MimeMessage(session).apply {
                        setFrom(InternetAddress("androidtestVKR@yandex.ru"))
                        setRecipients(Message.RecipientType.TO, InternetAddress.parse("d.palaumov@yandex.ru"))
                        subject = "Репортаж от $name"

                        // Текст письма
                        val textPart = MimeBodyPart().apply {
                            setText("""
                            Имя: $name
                            Дата: ${SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date())}
                            
                            Описание:
                            $description
                        """.trimIndent())
                        }

                        // Вложения
                        val multipart = MimeMultipart().apply {
                            addBodyPart(textPart)

                            // Обработка вложений
                            attachedFiles.forEach { uri ->
                                try {
                                    val filePath = getRealPathFromUri(uri) ?: run {
                                        Log.w("Attachment", "Cannot get path for uri: $uri")
                                        return@forEach  // Выход из текущей итерации forEach
                                    }

                                    val file = File(filePath)
                                    val maxSize = 25 * 1024 * 1024 // 25MB

                                    if (file.length() > maxSize) {
                                        Log.w("Attachment", "File too large: ${file.name}")
                                        return@forEach  // Выход из текущей итерации forEach
                                    }

                                    val attachmentPart = MimeBodyPart().apply {
                                        val source = FileDataSource(file)
                                        dataHandler = DataHandler(source)
                                        fileName = file.name
                                    }
                                    addBodyPart(attachmentPart)
                                } catch (e: Exception) {
                                    Log.e("AttachmentError", "Failed to attach file", e)
                                }
                            }
                        }

                        setContent(multipart)
                    }

                    Transport.send(message)
                    Pair(true, "Репортаж успешно отправлен!")
                } catch (e: Exception) {
                    Log.e("EmailError", "Failed to send email", e)
                    Pair(false, "Ошибка отправки: ${e.message ?: "неизвестная ошибка"}")
                }
            }

            override fun onPostExecute(result: Pair<Boolean, String>) {
                // Восстанавливаем UI
                progressBar.visibility = View.GONE
                buttonSend.isEnabled = true
                buttonAttachPhoto.isEnabled = true
                buttonAttachVideo.isEnabled = true

                // Показываем результат
                Toast.makeText(requireContext(), result.second, Toast.LENGTH_LONG).show()

                // Очищаем форму при успешной отправке
                if (result.first) {
                    clearForm()
                }
            }

            override fun onCancelled() {
                // На случай отмены задачи
                progressBar.visibility = View.GONE
                buttonSend.isEnabled = true
                buttonAttachPhoto.isEnabled = true
                buttonAttachVideo.isEnabled = true
            }
        }

        // Запускаем задачу
        emailTask.execute()
    }

    private fun MimeMultipart.addAttachments() {
        attachedFiles.forEach { uri ->
            try {
                val filePath = getRealPathFromUri(uri) ?: return@forEach
                val file = File(filePath)

                val attachmentPart = MimeBodyPart().apply {
                    val source: DataSource = FileDataSource(file)
                    dataHandler = DataHandler(source)
                    fileName = file.name
                }

                addBodyPart(attachmentPart)
            } catch (e: Exception) {
                Log.e("AttachmentError", "Failed to attach file", e)
            }
        }
    }

    private fun getRealPathFromUri(uri: Uri): String? {
        val projection = arrayOf("_data")
        val cursor = requireActivity().contentResolver.query(uri, projection, null, null, null)
        return cursor?.use {
            if (it.moveToFirst()) {
                it.getString(it.getColumnIndexOrThrow("_data"))
            } else null
        }
    }

    private fun clearForm() {
        editTextName.text.clear()
        editTextDescription.text.clear()
        attachedFiles.clear()
        layoutAttachedFiles.removeAllViews()
    }
}


