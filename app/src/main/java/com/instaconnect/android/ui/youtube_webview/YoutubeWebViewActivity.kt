package com.instaconnect.android.ui.youtube_webview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import com.instaconnect.android.R
import com.instaconnect.android.base.BaseIntent
import com.instaconnect.android.data.model.db.ChatMessage
import com.instaconnect.android.databinding.ActivityYoutubeWebViewBinding
import com.instaconnect.android.ui.video_preview.VideoPreviewsActivity
import com.instaconnect.android.utils.Constants
import java.util.regex.Pattern

class YoutubeWebViewActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding : ActivityYoutubeWebViewBinding

    private var captureType: String? = null
    private var url: String? = null

    private var categoryName: String? = null

    var videoUrl = ""
    var mp4VideoUrlCount = 0
    var plexTvUrlCount = 0
    var m3u8VideoUrlCount = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityYoutubeWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        categoryName = intent.getStringExtra("PostType")
        captureType = intent.getStringExtra("CaptureType")
        mp4VideoUrlCount = 0

        loadWebView()
        binding.ivBack.setOnClickListener(this)
    }

    private fun loadWebView() {
        val webSettings: WebSettings = binding.youtubeWebView.getSettings()
        webSettings.javaScriptEnabled = true
        webSettings.domStorageEnabled = true
        webSettings.builtInZoomControls = true
        url = if (intent.getStringExtra("URL_TO_LOAD") != null) {
            intent.getStringExtra("URL_TO_LOAD")
        } else {
            "https://www.youtube.com"
        }

//        binding.progressBar.setVisibility(View.VISIBLE);
        binding.youtubeWebView.loadUrl(url!!)
        binding.youtubeWebView.webChromeClient = MyWebChromeClient(this)
        binding.youtubeWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return super.shouldOverrideUrlLoading(view, url)
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                if (binding.progressBar.getVisibility() == View.VISIBLE) binding.progressBar.setVisibility(
                    View.VISIBLE)
            }

            override fun onLoadResource(view: WebView, url: String) {
                super.onLoadResource(view, url)
            }

            override fun doUpdateVisitedHistory(view: WebView, requestUrl: String, isReload: Boolean) {
                super.doUpdateVisitedHistory(view, requestUrl, isReload)
                Log.d("TAG", "shouldInterceptRequest: $requestUrl")
                if (requestUrl.contains("watch?v=") && requestUrl.contains("youtube.com")) {
                    val youtubeVideoId: String = extractYTId(requestUrl)!!
                    if (youtubeVideoId != null) {
                        showVideoPreview(youtubeVideoId, requestUrl, "youtube")
                    }
                }
            }

            override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
                val requestUrl = request.url.toString()
                Log.d("TAG", "shouldInterceptRequest xxxxx: $requestUrl")
                if (!requestUrl.contains("ads") && requestUrl.contains(".m3u8")) {
                    if (m3u8VideoUrlCount == 0) {
                        m3u8VideoUrlCount++
                        if (m3u8VideoUrlCount == 1) {
                            Log.d("TAG", "shouldInterceptRequest : .m3u8 $requestUrl")
                            showVideoPreview("", requestUrl, "stream")
                        }
                    }
                } else if (!requestUrl.contains("hdm.to") && requestUrl.contains(".mp4")) {
                    if (mp4VideoUrlCount == 0) {
                        mp4VideoUrlCount++
                        if (mp4VideoUrlCount == 1) {
                            Log.d("TAG", "shouldInterceptRequest : With mp4 $requestUrl")
                            showVideoPreview("", requestUrl, "stream")
                        }
                    }
                } else if (requestUrl.startsWith("https://epg.provider.plex.tv/") && requestUrl.contains("state=playing")) {
                    if (plexTvUrlCount == 0) {
                        plexTvUrlCount++
                        if (plexTvUrlCount == 1) {
                            Log.d("TAG", "shouldInterceptRequest : Plex TV $requestUrl")
                            showVideoPreview("", requestUrl, "stream")
                        }
                    }
                } else if (requestUrl.startsWith("https://vod.provider.plex.tv") && requestUrl.contains("state=playing")) {
                    if (plexTvUrlCount == 0) {
                        plexTvUrlCount++
                        if (plexTvUrlCount == 1) {
                            Log.d("TAG", "shouldInterceptRequest : Plex TV $requestUrl")
                            showVideoPreview("", requestUrl, "stream")
                        }
                    }
                }
                return null
            }
        }
    }

    private class MyWebChromeClient internal constructor(var context: Context) : WebChromeClient()


    fun extractYTId(ytUrl: String): String? {
        return if (ytUrl.contains("youtu.be")) {
            var vId = ""
            val pattern = Pattern.compile(
                "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
                Pattern.CASE_INSENSITIVE)
            val matcher = pattern.matcher(ytUrl)
            if (matcher.matches()) {
                vId = matcher.group(1)
            }
            vId
        } else {
            val pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*"
            val compiledPattern = Pattern.compile(pattern)
            val matcher = compiledPattern.matcher(ytUrl)
            if (matcher.find()) {
                matcher.group()
            } else ""
        }
    }

    fun showVideoPreview(youtubeVideoId: String?, hyperLink: String?, from: String) {
        Thread {
            val chatMessage = ChatMessage()
            chatMessage.dataType = Constants.VIDEO_TYPE
            chatMessage.postType = categoryName!!
            if (from == "youtube") {
                chatMessage.videoType = youtubeVideoId!!
            } else {
                chatMessage.videoType = hyperLink!!
            }
            chatMessage.captureType = captureType!!
            val intent = Intent(this@YoutubeWebViewActivity, VideoPreviewsActivity::class.java)
            intent.putExtra(Constants.CHAT_MESSAGE_MODEL, chatMessage)
            startActivity(intent)
            finish()
        }.start()
    }

    override fun onClick(v: View?) {

        when(v!!.id){
            R.id.iv_back -> finish()
        }
    }
}