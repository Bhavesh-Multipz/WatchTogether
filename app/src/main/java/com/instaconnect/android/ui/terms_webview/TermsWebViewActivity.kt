package com.instaconnect.android.ui.terms_webview

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.instaconnect.android.R
import com.instaconnect.android.databinding.ActivityTermsWebviewBinding
import com.instaconnect.android.network.ApiEndPoint

class TermsWebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTermsWebviewBinding
    var rlView: View? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermsWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rlView = findViewById<View>(R.id.progress);
        loadWebView()
    }

    private fun loadWebView() {
        val webSettings: WebSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true
        binding.webView.webViewClient = WebViewClient()
        binding.webView.setBackgroundColor(Color.TRANSPARENT)
        binding.webView.loadUrl("http://" + ApiEndPoint.IPADDRESS+ "/webapp/terms_and_conditions.html?mobileapp=1")
        showLoading()
        binding.ivBack.setOnClickListener(View.OnClickListener { finish() })
        binding.txtContinue.setOnClickListener(View.OnClickListener { finish() })
    }

    private fun showLoading() {
        rlView!!.visibility = View.VISIBLE
        Handler().postDelayed({ rlView!!.visibility = View.GONE }, 2000)
    }
}