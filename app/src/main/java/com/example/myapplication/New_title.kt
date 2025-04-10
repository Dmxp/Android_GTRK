package com.example.myapplication
//класс новостей для заголовков (новостной ленты)
data class News_title(
    val id: Int,
    val title: String,
    val published_date: String,
    val photo_path: String,
    val photo_name: String
)
