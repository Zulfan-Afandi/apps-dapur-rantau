package com.example.ui.components

import android.annotation.SuppressLint
import android.net.Uri
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun InteractiveMapView(
  query: String,
  latitude: Double,
  longitude: Double,
  cityName: String? = null,
  isScanning: Boolean = false,
  modifier: Modifier = Modifier,
  onErrorOccurred: (String) -> Unit = {}
) {
  var webViewRef by remember { mutableStateOf<WebView?>(null) }
  var isPageLoaded by remember { mutableStateOf(false) }

  AndroidView(
    modifier = modifier
      .fillMaxSize()
      .height(340.dp),
    factory = { ctx ->
      WebView(ctx).apply {
        settings.apply {
          javaScriptEnabled = true
          domStorageEnabled = true
          cacheMode = WebSettings.LOAD_DEFAULT
          allowFileAccess = true
          mediaPlaybackRequiresUserGesture = false
        }
        webChromeClient = WebChromeClient()
        webViewClient = object : WebViewClient() {
          override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            isPageLoaded = true
            val escapedQuery = Uri.encode(query)
            val escapedCity = cityName?.let { Uri.encode(it) } ?: ""
            view?.evaluateJavascript(
              "updateMap('$escapedQuery', $latitude, $longitude, '$escapedCity');",
              null
            )
          }

          override fun onReceivedError(
            view: WebView?,
            request: WebResourceRequest?,
            error: WebResourceError?
          ) {
            super.onReceivedError(view, request, error)
            val errorMsg = error?.description?.toString() ?: "WebView resource error"
            onErrorOccurred(errorMsg)
          }
        }
        loadUrl("file:///android_asset/map.html")
        webViewRef = this
      }
    },
    update = { _ ->
      // No-op here; handled by LaunchedEffect to avoid recomposition loops
    }
  )

  // Update map iframe when query, coordinates, or city change
  LaunchedEffect(query, latitude, longitude, cityName, isPageLoaded) {
    if (isPageLoaded && webViewRef != null) {
      val escapedQuery = Uri.encode(query)
      val escapedCity = cityName?.let { Uri.encode(it) } ?: ""
      webViewRef?.evaluateJavascript(
        "updateMap('$escapedQuery', $latitude, $longitude, '$escapedCity');",
        null
      )
    }
  }

  // Trigger CSS-only pulsing radar overlay when isScanning becomes true
  LaunchedEffect(isScanning) {
    if (isScanning && isPageLoaded && webViewRef != null) {
      webViewRef?.evaluateJavascript("triggerRadarScan();", null)
    }
  }

  DisposableEffect(Unit) {
    onDispose {
      webViewRef?.destroy()
      webViewRef = null
    }
  }
}
