package com.example.myapplication

import ProjectVideo
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProjectVideoAdapter(private val videos: List<ProjectVideo>) :
    RecyclerView.Adapter<ProjectVideoAdapter.VideoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_project_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(videos[position])
    }

    override fun getItemCount(): Int = videos.size

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title = itemView.findViewById<TextView>(R.id.tvVideoTitle)
        private val image = itemView.findViewById<ImageView>(R.id.ivVideoImage)

        fun bind(video: ProjectVideo) {
            title.text = video.title
            val imageUrl = "http://dpalaumovy.temp.swtest.ru${video.photopath}${video.photoname}"
            Glide.with(itemView.context).load(imageUrl).into(image)
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ProjectVideoActivity::class.java)
                intent.putExtra("video_id", video.id)
                itemView.context.startActivity(intent)
            }
        }
    }
}
