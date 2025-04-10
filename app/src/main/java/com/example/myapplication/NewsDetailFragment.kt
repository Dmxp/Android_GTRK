
package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.gson.Gson
import android.content.pm.ActivityInfo
import android.webkit.WebResourceRequest

class NewsDetailFragment : Fragment() {
    private var Id: Int = -1
    private lateinit var titleTextView: TextView
    private lateinit var dateTextView: TextView
    private lateinit var contentTextView: TextView
    private lateinit var imageView: ImageView
    private lateinit var videoLinkTextView: TextView
    private lateinit var webViewDetail: WebView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_news_detail, container, false)
        titleTextView = view.findViewById(R.id.news_detail_title)
        dateTextView = view.findViewById(R.id.news_detail_date)
        contentTextView = view.findViewById(R.id.news_detail_content)
        imageView = view.findViewById(R.id.news_detail_image)
        videoLinkTextView = view.findViewById(R.id.news_detail_video_link)
        webViewDetail = view.findViewById(R.id.webView)


        arguments?.let {
            Id = it.getInt("id", -1)
        }

        if (Id != -1) {
            fetchNewsDetails(Id)
        }

        return view
    }

    private fun fetchNewsDetails(Id: Int) {
        val url = "http://dpalaumovy.temp.swtest.ru/get_news_detail.php?id=$Id"

        val request = StringRequest(Request.Method.GET, url, { response ->
            val gson = Gson()
            val newsDetail = gson.fromJson(response, NewsDetail::class.java)

            titleTextView.text = newsDetail.title
            dateTextView.text = newsDetail.published_date
            contentTextView.text = newsDetail.text
            videoLinkTextView.text = newsDetail.video_link

            val imageUrl = "http://dpalaumovy.temp.swtest.ru/" + newsDetail.photo_path + newsDetail.photo_name
            Glide.with(this)
                .load(imageUrl)
                .into(imageView)
            // Проверка наличия ссылки на видео
            if (!newsDetail.video_link.isNullOrEmpty()) {
                // Если ссылка есть, делаем WebView видимым и загружаем видео
                webViewDetail.visibility = View.VISIBLE
                setupWebView(newsDetail.video_link)
            } else {
                // Если ссылки нет, оставляем WebView скрытым
                webViewDetail.visibility = View.GONE
            }
        }, {
            Toast.makeText(requireContext(), "Ошибка загрузки", Toast.LENGTH_SHORT).show()
        })

        Volley.newRequestQueue(requireContext()).add(request)
    }
    private fun setupWebView(videoUrl: String) {
        webViewDetail.settings.apply {
            javaScriptEnabled = true
            mediaPlaybackRequiresUserGesture = false
            allowFileAccess = true
            loadWithOverviewMode = true
            useWideViewPort = true
            builtInZoomControls = true
            displayZoomControls = false
        }
        webViewDetail.webChromeClient = object : WebChromeClient() {
            private var customView: View? = null
            private var customViewCallback: CustomViewCallback? = null
            private var originalSystemUiVisibility = 0
            private var originalOrientation = 0

            override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
                if (customView != null) {
                    callback?.onCustomViewHidden()
                    return
                }

                val activity = requireActivity()
                val decorView = activity.window.decorView as ViewGroup

                // Сохраняем текущее состояние UI и ориентацию
                originalSystemUiVisibility = decorView.systemUiVisibility
                originalOrientation = activity.requestedOrientation

                // Переходим в полноэкранный режим
                decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_FULLSCREEN
                                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        )

                customView = view
                customViewCallback = callback
                decorView.addView(customView, ViewGroup.LayoutParams.MATCH_PARENT)

                // Разрешаем поворот экрана
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
            }

            override fun onHideCustomView() {
                val activity = requireActivity()
                val decorView = activity.window.decorView as ViewGroup

                // Восстанавливаем исходное состояние UI и ориентацию
                decorView.systemUiVisibility = originalSystemUiVisibility
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

                // Удаляем полноэкранный вид
                customView?.let { decorView.removeView(it) }
                customView = null
                customViewCallback?.onCustomViewHidden()
            }
        }

        webViewDetail.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                view.loadUrl(request.url.toString())
                return true
            }
        }

        webViewDetail.loadUrl(videoUrl)
    }
    override fun onResume() {
        super.onResume()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (webViewDetail.canGoBack()) {
                        webViewDetail.goBack()
                    } else {
                        this.isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            })
    }
}

