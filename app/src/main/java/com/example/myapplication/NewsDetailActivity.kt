package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class NewsDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_detail)

        // Получаем id новости из интента
        val newsId = intent.getIntExtra("id", -1)
        if (newsId != -1) {
            val fragment = NewsDetailFragment().apply {
                arguments = Bundle().apply {
                    putInt("id", newsId)
                }
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.news_detail_container, fragment)
                .commit()
        } else {
            finish() // если id не передан, закрываем активити
        }
    }
}
