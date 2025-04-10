package com.example.myapplication

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.core.view.WindowCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var newsFragment: NewsFragment
    private lateinit var onlineFragment: OnlineFragment
    private lateinit var reporterFragment: ReporterFragment
    private lateinit var aboutFragment: AboutFragment
    private lateinit var projectsFragment: ProjectsFragment
    private var activeFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_main)

        // Инициализация фрагментов
        newsFragment = NewsFragment()
        onlineFragment = OnlineFragment()
        reporterFragment = ReporterFragment()
        aboutFragment = AboutFragment()
        projectsFragment = ProjectsFragment()

        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, projectsFragment, "5").hide(projectsFragment)
            .add(R.id.fragment_container, aboutFragment, "4").hide(aboutFragment)
            .add(R.id.fragment_container, reporterFragment, "3").hide(reporterFragment)
            .add(R.id.fragment_container, onlineFragment, "2").hide(onlineFragment)
            .add(R.id.fragment_container, newsFragment, "1")
            .commit()

        activeFragment = newsFragment

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_news -> switchFragment(newsFragment)
                R.id.nav_online -> switchFragment(onlineFragment)
                R.id.nav_reporter -> switchFragment(reporterFragment)
                R.id.nav_about -> switchFragment(aboutFragment)
                R.id.nav_projects -> switchFragment(projectsFragment)
            }
            true
        }
    }

    private fun switchFragment(targetFragment: Fragment) {
        if (targetFragment != activeFragment) {
            supportFragmentManager.beginTransaction()
                .hide(activeFragment!!)
                .show(targetFragment)
                .commit()
            activeFragment = targetFragment
        }
    }
}
