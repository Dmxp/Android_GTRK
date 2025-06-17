//package com.example.myapplication
package com.example.myapplication

import ProjectVideo
import android.graphics.Color
import android.net.Uri
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
import java.util.*

class ProjectDetailsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ProjectVideoAdapter
    private lateinit var searchView: SearchView

    private val videoList = mutableListOf<ProjectVideo>()
    private val loadedVideoIds = mutableSetOf<Int>() // Для предотвращения дублирования

    private var projectId: Int = -1
    private var currentPage = 1
    private val pageSize = 10
    private var isLoading = false
    private var isLastPage = false
    private var currentQuery: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_project_detail, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewProjectVideos)
        searchView = view.findViewById(R.id.searchViewProjectVideos)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ProjectVideoAdapter(videoList)
        recyclerView.adapter = adapter

        arguments?.let {
            projectId = it.getInt("project_id")
        }

        // Настройка поиска
        searchView.isIconified = false
        searchView.queryHint = "Поиск записей"
        val searchEdit = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEdit.setTextColor(Color.WHITE)
        searchEdit.setHintTextColor(Color.LTGRAY)

        // Обработка текста поиска
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                resetPaginationAndFetch(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                resetPaginationAndFetch(newText)
                return false
            }
        })

        // Слушатель на закрытие поиска
        searchView.setOnCloseListener {
            resetPaginationAndFetch(null)
            false
        }

        // Загрузка данных
        fetchProjectVideos(projectId)

        // Пагинация при прокрутке
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
                        fetchProjectVideos(projectId, currentQuery)
                    }
                }
            }
        })

        return view
    }

    private fun resetPaginationAndFetch(query: String?) {
        currentPage = 1
        currentQuery = query
        isLastPage = false
        loadedVideoIds.clear()
        videoList.clear()
        adapter.notifyDataSetChanged()
        fetchProjectVideos(projectId, query)
    }

    private fun fetchProjectVideos(projectId: Int, query: String? = null) {
        isLoading = true

        val url = if (query.isNullOrEmpty()) {
            "http://dpalaumovy.temp.swtest.ru/get_project_videos.php?project_id=$projectId&page=$currentPage"
        } else {
            "http://dpalaumovy.temp.swtest.ru/get_project_videos.php?project_id=$projectId&page=$currentPage&query=${Uri.encode(query)}"
        }

        val request = StringRequest(Request.Method.GET, url,
            { response ->
                val gson = Gson()
                val videos = gson.fromJson(response, Array<ProjectVideo>::class.java).toList()

                // Очистка списка только при первой загрузке (page == 1)
                if (currentPage == 1) {
                    videoList.clear()
                    loadedVideoIds.clear()
                }

                // Фильтрация уникальных видео
                val newVideos = videos.filter { it.id !in loadedVideoIds }
                loadedVideoIds.addAll(newVideos.map { it.id })
                videoList.addAll(newVideos)

                adapter.notifyDataSetChanged()

                isLoading = false
                isLastPage = newVideos.isEmpty()
            },
            {
                Toast.makeText(requireContext(), "Ошибка загрузки", Toast.LENGTH_SHORT).show()
                isLoading = false
            })

        Volley.newRequestQueue(requireContext()).add(request)
    }
}
//
//import ProjectVideo
//import android.graphics.Color
//import android.net.Uri
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
//import com.android.volley.toolbox.StringRequest
//import com.android.volley.toolbox.Volley
//import com.google.gson.Gson
//import com.android.volley.Request
//
//class ProjectDetailsFragment : Fragment() {
//
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var adapter: ProjectVideoAdapter
//    private lateinit var searchView: SearchView
//
//    private val videoList = mutableListOf<ProjectVideo>()
//    private var projectId: Int = -1
//    private var currentQuery: String? = null
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        val view = inflater.inflate(R.layout.fragment_project_detail, container, false)
//
//        recyclerView = view.findViewById(R.id.recyclerViewProjectVideos)
//        searchView = view.findViewById(R.id.searchViewProjectVideos)
//
//        recyclerView.layoutManager = LinearLayoutManager(requireContext())
//        adapter = ProjectVideoAdapter(videoList)
//        recyclerView.adapter = adapter
//
//        arguments?.let {
//            projectId = it.getInt("project_id")
//        }
//
//        // Настройка поиска
//        searchView.isIconified = false
//        searchView.queryHint = "Поиск записей"
//        val searchEdit = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
//        searchEdit.setTextColor(Color.WHITE)
//        searchEdit.setHintTextColor(Color.LTGRAY)
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                currentQuery = query
//                fetchProjectVideos(projectId, currentQuery)
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                currentQuery = newText
//                fetchProjectVideos(projectId, currentQuery)
//                return false
//            }
//        })
//
//        fetchProjectVideos(projectId)
//
//        return view
//    }
//
//    private fun fetchProjectVideos(projectId: Int, query: String? = null) {
//        val url = if (query.isNullOrEmpty()) {
//            "http://dpalaumovy.temp.swtest.ru/get_project_videos.php?project_id=$projectId"
//        } else {
//            "http://dpalaumovy.temp.swtest.ru/get_project_videos.php?project_id=$projectId&query=${Uri.encode(query)}"
//        }
//
//        val request = StringRequest(Request.Method.GET, url, { response ->
//            val gson = Gson()
//            val videos = gson.fromJson(response, Array<ProjectVideo>::class.java)
//            videoList.clear()
//            videoList.addAll(videos)
//            adapter.notifyDataSetChanged()
//        }, {
//            Toast.makeText(requireContext(), "Ошибка загрузки", Toast.LENGTH_SHORT).show()
//        })
//
//        Volley.newRequestQueue(requireContext()).add(request)
//    }
//}
//
