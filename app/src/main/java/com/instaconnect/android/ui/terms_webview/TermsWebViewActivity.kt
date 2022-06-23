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
import com.instaconnect.android.network.ApiEndPoint.IPADDRESS

class TermsWebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTermsWebviewBinding
    var rlView: View? = null
    var type: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTermsWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        type = intent.getStringExtra("TYPE")!!
        loadWebView()
    }

    private fun loadWebView() {
        rlView = findViewById(R.id.progress)
        binding.ivBack.visibility = View.VISIBLE
        val webSettings: WebSettings = binding.webView.settings
        webSettings.javaScriptEnabled = true
        binding.webView.webViewClient = WebViewClient()
        binding.webView.setBackgroundColor(Color.TRANSPARENT)
        if (type == "Privacy") {
            binding.tvTitle.text = "Privacy Policy"
            binding.checkbox.text = "I agree with the Privacy Policy"
            binding.webView.loadUrl("http://99.79.19.208/webapp/privacy_policy.html?mobileapp=1")
        } else {
            binding.tvTitle.text = "Terms & Condition"
            binding.checkbox.text = "I agree with the terms and condition"
            binding.webView.loadUrl("http://99.79.19.208/webapp/terms_and_conditions.html?mobileapp=1")
        }

        showLoading()
        binding.ivBack.setOnClickListener { finish() }
        binding.txtContinue.setOnClickListener { finish() }
    }

    private fun showLoading() {
        Handler().postDelayed({
            rlView?.visibility = View.GONE
        }, 2000)
    }
}