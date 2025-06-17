package com.example.myapplication

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson

class NewsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var newsAdapter: NewsAdapter
    private val newsList = mutableListOf<News_title>()
    // Переменные для пагинации
    private var currentPage = 1
    private val pageSize = 10
    private var isLoading = false
    private var isLastPage = false
    private val loadedNewsIds = mutableSetOf<Int>()
    private lateinit var searchView: SearchView
    private var currentQuery: String? = null // Для хранения текущего поискового запроса

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.news_fragment, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        searchView = view.findViewById(R.id.searchView)

        // Настройка строки поиска
        searchView.isIconified = false
        searchView.clearFocus()
        searchView.queryHint = "Поиск новостей"
        val searchText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchText.setTextColor(Color.WHITE)
        searchText.setHintTextColor(Color.LTGRAY)

        // Обработка поискового запроса
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                currentPage = 1
                currentQuery = query
                fetchNews(currentPage, query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                currentPage = 1
                currentQuery = newText
                fetchNews(currentPage, newText)
                return false
            }
        })

        // Слушатель для крестика (очистки)
        searchView.setOnCloseListener {
            searchView.setQuery("", false) // Очистить текст
            searchView.queryHint = "Поиск новостей" // Вернуть подсказку
            currentPage = 1
            currentQuery = null
            fetchNews(currentPage)
            false
        }

        // Если уже есть новости — не загружаем снова
        if (newsList.isEmpty()) {
            fetchNews(currentPage)
        } else {
            newsAdapter = NewsAdapter(newsList) { selectedNews ->
                openDetailFragment(selectedNews.id)
            }
            recyclerView.adapter = newsAdapter
        }

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
                        fetchNews(currentPage, currentQuery)
                    }
                }
            }
        })
        return view
    }

    private fun fetchNews(page: Int, query: String? = null) {
        isLoading = true
        val url = if (query.isNullOrEmpty()) {
            "http://dpalaumovy.temp.swtest.ru/get_news_title1.php?page=$page"
        } else {
            "http://dpalaumovy.temp.swtest.ru/get_news_title1.php?page=$page&query=$query"
        }

        val request = StringRequest(Request.Method.GET, url, { response ->
            val gson = Gson()
            val newsArray = gson.fromJson(response, Array<News_title>::class.java).toList()

            // Очищаем список новостей, если это первый запрос для нового поиска
            if (page == 1) {
                newsList.clear()
                loadedNewsIds.clear()
            }

            // Добавляем только уникальные новости
            val newNews = newsArray.filter { it.id !in loadedNewsIds }
            loadedNewsIds.addAll(newNews.map { it.id })

            if (newNews.isNotEmpty()) {
                newsList.addAll(newNews)
                if (!::newsAdapter.isInitialized) {
                    newsAdapter = NewsAdapter(newsList) { selectedNews ->
                        openDetailFragment(selectedNews.id)
                    }
                    recyclerView.adapter = newsAdapter
                } else {
                    newsAdapter.notifyDataSetChanged()
                }
            }

            isLoading = false
            isLastPage = newNews.isEmpty()

        }, {
            Toast.makeText(requireContext(), "Ошибка загрузки", Toast.LENGTH_SHORT).show()
            isLoading = false
        })

        Volley.newRequestQueue(requireContext()).add(request)
    }

    private fun openDetailFragment(newsId: Int) {
        val intent = Intent(requireContext(), NewsDetailActivity::class.java)
        intent.putExtra("id", newsId)
        startActivity(intent)
    }
}


