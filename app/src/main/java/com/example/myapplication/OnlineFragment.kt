package com.example.myapplication
//фрагмент онлайн трансляций
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
        return inflater.inflate(R.layout.fragmetn_online, container, false)
    }
}
