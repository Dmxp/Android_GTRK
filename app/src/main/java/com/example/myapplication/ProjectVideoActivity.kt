package com.example.myapplication

import ProjectVideo
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.android.volley.Request

class ProjectVideoActivity : AppCompatActivity() {

    private lateinit var tvTitle: TextView
    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_video)

        tvTitle = findViewById(R.id.tvVideoTitle)
        webView = findViewById(R.id.webViewVideo)

        val videoId = intent.getIntExtra("video_id", -1)
        if (videoId == -1) {
            Toast.makeText(this, "Неверный ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val url = "http://dpalaumovy.temp.swtest.ru/get_project_video_detail.php?id=$videoId"

        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                val video = Gson().fromJson(response, ProjectVideo::class.java)
                tvTitle.text = video.title
                setupWebView(video.link)
            },
            {
                Toast.makeText(this, "Ошибка загрузки", Toast.LENGTH_SHORT).show()
                finish()
            })

        Volley.newRequestQueue(this).add(request)
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun setupWebView(link: String) {
        webView.settings.apply {
            javaScriptEnabled = true
            mediaPlaybackRequiresUserGesture = false
            loadWithOverviewMode = true
            useWideViewPort = true
            allowFileAccess = true
            builtInZoomControls = true
            displayZoomControls = false
        }

        webView.webChromeClient = object : WebChromeClient() {
            private var customView: View? = null
            private var customViewCallback: CustomViewCallback? = null
            private var originalSystemUiVisibility = 0
            private var originalOrientation = 0

            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                if (customView != null) {
                    callback?.onCustomViewHidden()
                    return
                }

                val decor = window.decorView as ViewGroup
                originalSystemUiVisibility = decor.systemUiVisibility
                originalOrientation = requestedOrientation

                customView = view
                customViewCallback = callback
                decor.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        )

                decor.addView(view, ViewGroup.LayoutParams.MATCH_PARENT)
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
            }

            override fun onHideCustomView() {
                val decor = window.decorView as ViewGroup
                decor.systemUiVisibility = originalSystemUiVisibility
                requestedOrientation = originalOrientation
                customView?.let { decor.removeView(it) }
                customView = null
                customViewCallback?.onCustomViewHidden()
            }
        }

        webView.webViewClient = WebViewClient()
        webView.loadUrl(link)
    }
}
