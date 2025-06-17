//package com.example.myapplication
package com.example.myapplication

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson

class StreamActivity : AppCompatActivity() {

    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var webView: WebView

    private var customView: View? = null
    private var customViewCallback: WebChromeClient.CustomViewCallback? = null
    private var originalSystemUiVisibility = 0
    private var originalOrientation = 0

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stream)

        titleTextView = findViewById(R.id.tv_stream_title)
        descriptionTextView = findViewById(R.id.tv_stream_description)
        webView = findViewById(R.id.webview_stream)

        val streamTitle = intent.getStringExtra("stream_title") ?: return

        val url = "http://dpalaumovy.temp.swtest.ru/get_stream.php?title=${streamTitle}"

        val request = StringRequest(Request.Method.GET, url, { response ->
            val gson = Gson()
            val streamData = gson.fromJson(response, StreamData::class.java)

            titleTextView.text = streamData.title
            descriptionTextView.text = streamData.text

            setupWebView(streamData.link)

        }, {
            Toast.makeText(this, "Ошибка загрузки трансляции", Toast.LENGTH_SHORT).show()
        })

        Volley.newRequestQueue(this).add(request)
    }

    private fun setupWebView(videoUrl: String) {
        webView.settings.apply {
            javaScriptEnabled = true
            mediaPlaybackRequiresUserGesture = false
            allowFileAccess = true
            loadWithOverviewMode = true
            useWideViewPort = true
            builtInZoomControls = true
            displayZoomControls = false
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                if (customView != null) {
                    callback?.onCustomViewHidden()
                    return
                }

                val decorView = window.decorView as ViewGroup
                originalSystemUiVisibility = decorView.systemUiVisibility
                originalOrientation = requestedOrientation

                customView = view
                customViewCallback = callback
                decorView.addView(view, ViewGroup.LayoutParams.MATCH_PARENT)

                decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        )

                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
            }

            override fun onHideCustomView() {
                val decorView = window.decorView as ViewGroup
                decorView.systemUiVisibility = originalSystemUiVisibility
                requestedOrientation = originalOrientation

                customView?.let { decorView.removeView(it) }
                customView = null
                customViewCallback?.onCustomViewHidden()
            }
        }

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                view.loadUrl(request.url.toString())
                return true
            }
        }

        webView.loadUrl(videoUrl)
    }

    data class StreamData(
        val title: String,
        val text: String,
        val link: String
    )
}

//
//import android.annotation.SuppressLint
//import android.os.Bundle
//import android.webkit.WebChromeClient
//import android.webkit.WebView
//import android.webkit.WebViewClient
//import android.widget.TextView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.android.volley.Request
//import com.android.volley.toolbox.StringRequest
//import com.android.volley.toolbox.Volley
//import com.google.gson.Gson
//
//class StreamActivity : AppCompatActivity() {
//
//    private lateinit var titleTextView: TextView
//    private lateinit var descriptionTextView: TextView
//    private lateinit var webView: WebView
//
//    @SuppressLint("SetJavaScriptEnabled")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_stream)
//
//        titleTextView = findViewById(R.id.tv_stream_title)
//        descriptionTextView = findViewById(R.id.tv_stream_description)
//        webView = findViewById(R.id.webview_stream)
//
//        val streamTitle = intent.getStringExtra("stream_title") ?: return
//
//        val url = "http://dpalaumovy.temp.swtest.ru/get_stream.php?title=${streamTitle}"
//
//        val request = StringRequest(Request.Method.GET, url, { response ->
//            val gson = Gson()
//            val streamData = gson.fromJson(response, StreamData::class.java)
//
//            titleTextView.text = streamData.title
//            descriptionTextView.text = streamData.text
//
//            webView.settings.javaScriptEnabled = true
//            webView.settings.mediaPlaybackRequiresUserGesture = false
//            webView.webChromeClient = WebChromeClient()
//            webView.webViewClient = WebViewClient()
//            webView.loadUrl(streamData.link)
//
//        }, {
//            Toast.makeText(this, "Ошибка загрузки трансляции", Toast.LENGTH_SHORT).show()
//        })
//
//        Volley.newRequestQueue(this).add(request)
//    }
//
//    data class StreamData(
//        val title: String,
//        val text: String,
//        val link: String
//    )
//}
