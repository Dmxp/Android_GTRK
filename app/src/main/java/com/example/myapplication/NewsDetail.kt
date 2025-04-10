package com.example.myapplication
//класс новостей для детального отображения конкретной новости
data class NewsDetail(
    val id: Int,
    val title: String,
    val text: String,
    val photo_path: String,
    val photo_name: String,
    val video_link: String,
    val published_date: String
)