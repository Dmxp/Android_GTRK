package com.example.myapplication

import Project
import android.os.Bundle
import android.provider.Settings.Global.putInt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProjectAdapter(
    private val projects: List<Project>,
    private val onItemClick: (Project) -> Unit
) : RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.project_item, parent, false)
        return ProjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
        val project = projects[position]
        holder.bind(project)
        // Обработка клика
        holder.itemView.setOnClickListener {
            val fragment = ProjectDetailsFragment().apply {
                arguments = Bundle().apply {
                    putInt("project_id", project.id)
                }
            }
            val context = holder.itemView.context
            if (context is AppCompatActivity) {
                context.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun getItemCount(): Int = projects.size

    class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.tvProjectTitle)
        private val imageView: ImageView = itemView.findViewById(R.id.ivProjectImage)

        fun bind(project: Project) {
            titleTextView.text = project.title
            val imageUrl = "http://dpalaumovy.temp.swtest.ru${project.photopath}${project.photoname}"
            Glide.with(itemView.context)
                .load(imageUrl)
                .into(imageView)
        }
    }
}

//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.example.myapplication.R
//
//class ProjectAdapter(
//    private val projects: List<Project>,
//    private val onItemClick: (Project) -> Unit
//) : RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder>() {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProjectViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.project_item, parent, false)
//        return ProjectViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ProjectViewHolder, position: Int) {
//        holder.bind(projects[position])
//        holder.itemView.setOnClickListener { onItemClick(projects[position]) }
//
//    }
//
//    override fun getItemCount() = projects.size
//
//    class ProjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val title = itemView.findViewById<TextView>(R.id.tvProjectTitle)
//        private val image = itemView.findViewById<ImageView>(R.id.ivProjectImage)
//
//        fun bind(project: Project) {
//            title.text = project.title
//            val imageUrl = "http://dpalaumovy.temp.swtest.ru${project.photopath}${project.photoname}"
//            Glide.with(itemView.context).load(imageUrl).into(image)
//        }
//    }
//}
