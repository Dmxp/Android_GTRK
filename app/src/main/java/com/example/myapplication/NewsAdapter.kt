package com.example.myapplication
//адаптер для отображения новостей
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import com.google.gson.Gson
import android.widget.Filter
import android.widget.Filterable


class NewsAdapter(
    private val newsList: List<News_title>,
    private val onItemClick: (News_title) -> Unit
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>(), Filterable {

    private var filteredNewsList = newsList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = newsList[position]
        holder.bind(news)
        holder.itemView.setOnClickListener { onItemClick(news) }
    }

    override fun getItemCount(): Int = newsList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.lowercase()?.trim() ?: ""
                val results = if (query.isEmpty()) {
                    newsList
                } else {
                    newsList.filter {
                        it.title.lowercase().contains(query)
                    }
                }
                return FilterResults().apply { values = results }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredNewsList = results?.values as List<News_title>
                notifyDataSetChanged()
            }
        }
    }

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.tvTitle)
        private val dateTextView: TextView = itemView.findViewById(R.id.tvPublishDate)
        private val imageView: ImageView = itemView.findViewById(R.id.news_image)


        fun bind(news: News_title) {
            titleTextView.text = news.title
            dateTextView.text = news.published_date

            // Загружаем фото через Glide
            val imageUrl = "http://dpalaumovy.temp.swtest.ru/${news.photo_path}${news.photo_name}"
            Glide.with(itemView.context)
                .load(imageUrl)
                .into(imageView)
        }
    }
}