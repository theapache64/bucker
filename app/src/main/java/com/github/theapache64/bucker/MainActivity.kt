package com.github.theapache64.bucker

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

@SuppressLint("SetJavaScriptEnabled")
class MainActivity : AppCompatActivity() {

    private val allowList = listOf(
        "reddit.com",
        "medium.com",
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
                        return (allowList.find { allowedDomain ->
                            host == allowedDomain || host == "www.$allowedDomain"
                        } == null).also { shouldBlock ->
                            if(shouldBlock){
                                Toast.makeText(this@MainActivity, "$host is trash bruh! am not loading it! \uD83D\uDE45", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
    }

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
            Toast.makeText(this, "No URL passed", Toast.LENGTH_LONG).show()
            finish()
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