package com.example.ui.components

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun VideoPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(Color.Black)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                WebView(context).apply {
                    webViewClient = WebViewClient()
                    webChromeClient = WebChromeClient()
                    isFocusable = false
                    isFocusableInTouchMode = false
                    isClickable = false
                    
                    settings.apply {
                        javaScriptEnabled = true
                        mediaPlaybackRequiresUserGesture = false
                        pluginState = WebSettings.PluginState.ON
                        domStorageEnabled = true
                        useWideViewPort = true
                        loadWithOverviewMode = true
                    }
                }
            },
            update = { webView ->
                val embedUrl = resolveEmbedUrl(videoUrl)
                val htmlPayload = """
                    <html>
                    <body style="margin:0;padding:0;background-color:#000000;">
                        <iframe width="100%" height="100%" 
                                src="$embedUrl" 
                                frameborder="0" 
                                allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" 
                                allowfullscreen>
                        </iframe>
                    </body>
                    </html>
                """.trimIndent()
                
                webView.loadDataWithBaseURL("https://www.youtube.com", htmlPayload, "text/html", "utf-8", null)
            }
        )
    }
}

/**
 * Ensures standard user-entered links (e.g., share links) are converted to embeddable player links.
 */
private fun resolveEmbedUrl(url: String): String {
    if (url.contains("embed")) {
        return if (!url.contains("?")) {
            "$url?autoplay=1&loop=1&controls=0&playsinline=1"
        } else {
            url
        }
    }
    
    // YouTube short links and standard watch links
    return try {
        if (url.contains("youtu.be/")) {
            val videoId = url.substringAfter("youtu.be/").substringBefore("?").substringBefore("/")
            "https://www.youtube.com/embed/$videoId?autoplay=1&loop=1&playlist=$videoId&controls=0&playsinline=1"
        } else if (url.contains("watch?v=")) {
            val videoId = url.substringAfter("watch?v=").substringBefore("&").substringBefore("/")
            "https://www.youtube.com/embed/$videoId?autoplay=1&loop=1&playlist=$videoId&controls=0&playsinline=1"
        } else if (url.contains("shorts/")) {
            val videoId = url.substringAfter("shorts/").substringBefore("?").substringBefore("/")
            "https://www.youtube.com/embed/$videoId?autoplay=1&loop=1&playlist=$videoId&controls=0&playsinline=1"
        } else if (url.contains("drive.google.com/file/d/")) {
            val fileId = url.substringAfter("/d/").substringBefore("/view").substringBefore("/preview")
            "https://drive.google.com/file/d/$fileId/preview?autoplay=1"
        } else {
            url
        }
    } catch (e: Exception) {
        url
    }
}
