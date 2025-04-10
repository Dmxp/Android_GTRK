package com.example.myapplication

import EmailSender
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ReporterFragment : Fragment() {

    private lateinit var editTextName: EditText
    private lateinit var editTextContact: EditText
    private lateinit var buttonAttachFiles: Button
    private lateinit var buttonSend: Button
    private lateinit var recyclerViewFiles: RecyclerView
    private val fileUris = mutableListOf<Uri>()
    private lateinit var adapter: FileAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reporter, container, false)

        editTextName = view.findViewById(R.id.editTextName)
        editTextContact = view.findViewById(R.id.editTextContact)
        buttonAttachFiles = view.findViewById(R.id.buttonAttachFiles)
        buttonSend = view.findViewById(R.id.buttonSend)
        recyclerViewFiles = view.findViewById(R.id.recyclerViewFiles)

        adapter = FileAdapter(fileUris)
        recyclerViewFiles.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewFiles.adapter = adapter

        buttonAttachFiles.setOnClickListener { openFileChooser() }
        buttonSend.setOnClickListener { sendEmail() }

        return view
    }

    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris ->
        if (fileUris.size + uris.size > 5) {
            Toast.makeText(requireContext(), "Можно прикрепить не более 5 файлов", Toast.LENGTH_SHORT).show()
        } else {
            fileUris.addAll(uris)
            adapter.notifyDataSetChanged()
        }
    }

    private fun openFileChooser() {
        filePickerLauncher.launch("*/*") // Позволяет выбрать фото и видео
    }
    private fun sendEmail() {
        val name = editTextName.text.toString().trim()
        val contact = editTextContact.text.toString().trim()

        if (name.isEmpty() || contact.isEmpty()) {
            Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
            return
        }

        val subject = "Сообщение от мобильного репортера"
        val body = "Имя: $name\nКонтакты: $contact"

        val emailSender = EmailSender()
        emailSender.sendEmail(subject, body, fileUris)

        Toast.makeText(requireContext(), "Письмо отправлено", Toast.LENGTH_SHORT).show()
    }
//    private fun sendEmail() {
//        val name = editTextName.text.toString().trim()
//        val contact = editTextContact.text.toString().trim()
//
//        if (name.isEmpty() || contact.isEmpty()) {
//            Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val emailIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
//            type = "message/rfc822"
//            putExtra(Intent.EXTRA_EMAIL, arrayOf("d.palaumov@yandex.ru")) // Укажи свою почту редакции
//            putExtra(Intent.EXTRA_SUBJECT, "Сообщение от мобильного репортера")
//            putExtra(Intent.EXTRA_TEXT, "Имя: $name\nКонтакты: $contact")
//
//            if (fileUris.isNotEmpty()) {
//                putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(fileUris))
//            }
//        }
//
//        try {
//            startActivity(Intent.createChooser(emailIntent, "Выберите почтовое приложение"))
//        } catch (e: Exception) {
//            Toast.makeText(requireContext(), "Нет доступных почтовых приложений", Toast.LENGTH_SHORT).show()
//        }
//    }
}
