package com.example.myapplication
//фрагмент проектов
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class ProjectsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Подключаем разметку фрагмента
        return inflater.inflate(R.layout.fragment_projects, container, false)
    }
}
