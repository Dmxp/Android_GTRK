//package com.example.myapplication
package com.example.myapplication

import Project
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson

class ProjectsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProjectAdapter
    private lateinit var searchView: SearchView

    private val projectList = mutableListOf<Project>()
    private val loadedProjectIds = mutableSetOf<Int>() // Для отслеживания загруженных проектов
    private var currentPage = 1
    private val pageSize = 10
    private var isLoading = false
    private var isLastPage = false
    private var currentQuery: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_projects, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewProjects)
        searchView = view.findViewById(R.id.searchViewProjects)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Настройка поиска
        setupSearchView()

        // Инициализация адаптера
        adapter = ProjectAdapter(projectList) { project ->
            // Обработка клика по проекту
        }
        recyclerView.adapter = adapter

        // Добавляем слушатель прокрутки для пагинации
        setupScrollListener()

        // Первоначальная загрузка данных
        if (projectList.isEmpty()) {
            fetchProjects(currentPage)
        }

        return view
    }

    private fun setupSearchView() {
        searchView.isIconified = false
        searchView.clearFocus()
        searchView.queryHint = "Поиск проектов"
        val searchText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchText.setTextColor(Color.WHITE)
        searchText.setHintTextColor(Color.LTGRAY)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                currentPage = 1
                currentQuery = query
                fetchProjects(currentPage, query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                currentPage = 1
                currentQuery = newText
                fetchProjects(currentPage, newText)
                return false
            }
        })

        searchView.setOnCloseListener {
            searchView.setQuery("", false)
            searchView.queryHint = "Поиск проектов"
            currentPage = 1
            currentQuery = null
            fetchProjects(currentPage)
            false
        }
    }

    private fun setupScrollListener() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount &&
                        firstVisibleItemPosition >= 0
                    ) {
                        currentPage++
                        fetchProjects(currentPage, currentQuery)
                    }
                }
            }
        })
    }

    private fun fetchProjects(page: Int, query: String? = null) {
        isLoading = true
        val url = if (query.isNullOrEmpty()) {
            "http://dpalaumovy.temp.swtest.ru/get_projects.php?page=$page"
        } else {
            "http://dpalaumovy.temp.swtest.ru/get_projects.php?page=$page&query=$query"
        }

        val request = StringRequest(Request.Method.GET, url, { response ->
            val gson = Gson()
            val projectsArray = gson.fromJson(response, Array<Project>::class.java).toList()

            // Очищаем список, если это первая страница нового запроса
            if (page == 1) {
                projectList.clear()
                loadedProjectIds.clear()
            }

            // Добавляем только уникальные проекты
            val newProjects = projectsArray.filter { it.id !in loadedProjectIds }
            loadedProjectIds.addAll(newProjects.map { it.id })

            if (newProjects.isNotEmpty()) {
                projectList.addAll(newProjects)
                if (!::adapter.isInitialized) {
                    adapter = ProjectAdapter(projectList) { project ->
                        // Обработка клика
                    }
                    recyclerView.adapter = adapter
                } else {
                    adapter.notifyDataSetChanged()
                }
            }

            isLoading = false
            isLastPage = newProjects.isEmpty()

        }, {
            Toast.makeText(requireContext(), "Ошибка загрузки проектов", Toast.LENGTH_SHORT).show()
            isLoading = false
        })

        Volley.newRequestQueue(requireContext()).add(request)
    }
}
//
//import Project
//import android.graphics.Color
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.EditText
//import android.widget.Toast
//import androidx.appcompat.widget.SearchView
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.android.volley.Request
//import com.android.volley.toolbox.StringRequest
//import com.android.volley.toolbox.Volley
//import com.google.gson.Gson
//
//class ProjectsFragment : Fragment() {
//
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapter: ProjectAdapter
//    private lateinit var searchView: SearchView
//
//    private val projectList = mutableListOf<Project>()
//    private var currentPage = 1
//    private var isLoading = false
//    private var isLastPage = false
//    private var currentQuery: String? = null
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
//        val view = inflater.inflate(R.layout.fragment_projects, container, false)
//
//        recyclerView = view.findViewById(R.id.recyclerViewProjects)
//        searchView = view.findViewById(R.id.searchViewProjects)
//
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//
//        adapter = ProjectAdapter(projectList) { project ->
//        }
//        recyclerView.adapter = adapter
//
//        // Настройка поиска
//        searchView.isIconified = false
//        searchView.queryHint = "Поиск проектов"
//        val searchEdit = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
//        searchEdit.setTextColor(Color.WHITE)
//        searchEdit.setHintTextColor(Color.LTGRAY)
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                currentPage = 1
//                currentQuery = query
//                fetchProjects(currentPage, query)
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                currentPage = 1
//                currentQuery = newText
//                fetchProjects(currentPage, newText)
//                return false
//            }
//        })
//
//        fetchProjects(currentPage)
//
//        return view
//    }
//
//    private fun fetchProjects(page: Int, query: String? = null) {
//        isLoading = true
//        val url = if (query.isNullOrEmpty()) {
//            "http://dpalaumovy.temp.swtest.ru/get_projects.php?page=$page"
//        } else {
//            "http://dpalaumovy.temp.swtest.ru/get_projects.php?page=$page&query=$query"
//        }
//
//        val request = StringRequest(Request.Method.GET, url, { response ->
//            val gson = Gson()
//            val projects = gson.fromJson(response, Array<Project>::class.java).toList()
//
//            if (page == 1) projectList.clear()
//            projectList.addAll(projects)
//            adapter.notifyDataSetChanged()
//
//            isLastPage = projects.isEmpty()
//            isLoading = false
//
//        }, {
//            Toast.makeText(requireContext(), "Ошибка загрузки проектов", Toast.LENGTH_SHORT).show()
//            isLoading = false
//        })
//
//        Volley.newRequestQueue(requireContext()).add(request)
//    }
//}
