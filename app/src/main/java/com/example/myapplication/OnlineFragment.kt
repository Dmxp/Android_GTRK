package com.example.myapplication
//фрагмент онлайн трансляций
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class OnlineFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Подключаем разметку фрагмента
        val view = inflater.inflate(R.layout.fragmetn_online, container, false)

        // Находим кнопки по их id
        val btnRussia1 = view.findViewById<View>(R.id.btn_russia1)
        val btnRussia24 = view.findViewById<View>(R.id.btn_russia24)
        val btnUral24 = view.findViewById<View>(R.id.btn_ural_24)
        val btnRadioRussia = view.findViewById<View>(R.id.btn_radio_russia)
        val btnRadioMayak = view.findViewById<View>(R.id.btn_radio_mayak)
        val btnRadioVestiFM = view.findViewById<View>(R.id.btn_radio_vesti_fm)
        // Обработчик нажатия для кнопки "РОССИЯ 1"
        btnRussia1.setOnClickListener {
            val intent = Intent(requireContext(), StreamActivity::class.java)
            intent.putExtra("stream_title", "РОССИЯ 1") // Название кнопки
            startActivity(intent)
        }

        // Обработчик нажатия для кнопки "РОССИЯ 24"
        btnRussia24.setOnClickListener {
            val intent = Intent(requireContext(), StreamActivity::class.java)
            intent.putExtra("stream_title", "РОССИЯ 24")
            startActivity(intent)
        }

        // Обработчик нажатия для кнопки "Радио России"
        btnRadioRussia.setOnClickListener {
            val intent = Intent(requireContext(), StreamActivity::class.java)
            intent.putExtra("stream_title", "Радио России")
            startActivity(intent)
        }
        // Обработчик нажатия для кнопки "УРАЛ 24"
        btnUral24.setOnClickListener{
            val intent = Intent(requireContext(), StreamActivity::class.java)
            intent.putExtra("stream_title", "УРАЛ 24")
            startActivity(intent)
        }

        // Обработчик нажатия для кнопки "Радио Маяк"
        btnRadioMayak.setOnClickListener {
            val intent = Intent(requireContext(), StreamActivity::class.java)
            intent.putExtra("stream_title", "Радио Маяк")
            startActivity(intent)
        }

        // Обработчик нажатия для кнопки "Радио Вести FM"
        btnRadioVestiFM.setOnClickListener {
            val intent = Intent(requireContext(), StreamActivity::class.java)
            intent.putExtra("stream_title", "Радио Вести FM")
            startActivity(intent)
        }

        return view
    }
}
