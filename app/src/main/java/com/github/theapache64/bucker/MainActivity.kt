package com.github.theapache64.bucker

import android.R.attr.orientation
import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.net.URL

@SuppressLint("SetJavaScriptEnabled")
class MainActivity : AppCompatActivity() {

    private val allowMap = mapOf<String, String>(
        "reddit.com" to "https://reddit.com/r/programming",
        "medium.com" to "https://medium.com/androiddevelopers",
        "ycombinator.com" to "https://news.ycombinator.com",
        "tldr.tech" to "https://tldr.tech/",
        // add more websites here. don't forget to add it in manifest file
    )

    private val wvBucker by lazy {
        findViewById<WebView>(R.id.wv_bucker)
            .apply {
                settings.apply {
                    javaScriptEnabled = true
                }
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest?,
                    ): Boolean {
                        val host = request?.url?.host ?: return false
                        val allowedDomains = allowMap.keys
                        return (allowedDomains.find { allowedDomain ->
                            host == allowedDomain || host == "www.$allowedDomain"
                        } == null).let { shouldBlock ->
                            // Current host
                            val currentHost = URL(view?.url).host ?: ""
                            val isFromAllowedDomain = allowedDomains.find { allowedDomain ->
                                currentHost.endsWith(allowedDomain)
                            } != null
                            if (shouldBlock && !isFromAllowedDomain) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "$host is trash bruh! am not loading it! \uD83D\uDE45",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            shouldBlock && !isFromAllowedDomain
                        }
                    }
                }
            }
    }
    private lateinit var linearLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val incomingUrl = intent.data.toString()
        if (incomingUrl.isNotBlank() && incomingUrl != "null") {
            wvBucker.loadUrl(incomingUrl)
        } else {
            val container = findViewById<FrameLayout>(R.id.main)
            linearLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                for ((domain, url) in allowMap) {
                    val button = Button(this@MainActivity).apply {
                        text = "Open $domain"
                        setOnClickListener {
                            container.removeView(linearLayout)
                            wvBucker.loadUrl(url)
                        }
                    }
                    addView(button)
                }
            }
            container.addView(linearLayout)
        }

        onBackPressedDispatcher.addCallback {
            if (wvBucker.canGoBack()) {
                wvBucker.goBack()
            } else {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }


}