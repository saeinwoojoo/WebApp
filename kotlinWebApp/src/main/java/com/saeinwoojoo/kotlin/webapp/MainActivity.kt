package com.saeinwoojoo.kotlin.webapp

import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.webkit.WebSettings
import android.webkit.WebView

import com.saeinwoojoo.kotlin.webapp.webview.WebChromeClientEx
import com.saeinwoojoo.kotlin.webapp.webview.WebViewClientEx

/**
 * Shows the web content in the WebView.
 */
class MainActivity : BaseActivity() {

    private var mWebView: WebView? = null
    private var mFabExitApp: FloatingActionButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        HOME_URL = resources.getString(R.string.home_url)

        mWebView = findViewById<WebView>(R.id.web_view)
        if (null != mWebView) {
            val webSettings = mWebView?.settings
            if (null != webSettings) {
                webSettings.javaScriptEnabled = true
                webSettings.builtInZoomControls = true
                webSettings.displayZoomControls = false
                webSettings.setSupportZoom(true)
                webSettings.loadWithOverviewMode = true
                webSettings.useWideViewPort = true
//                webSettings.pluginState = WebSettings.PluginState.ON
                webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
                webSettings.javaScriptCanOpenWindowsAutomatically = true
//                webSettings.userAgentString = USER_AGENT
            }

            mWebView?.clearCache(true)
            mWebView?.webChromeClient = WebChromeClientEx(this)
            mWebView?.webViewClient = WebViewClientEx(this)
            mWebView?.loadUrl(HOME_URL)
        }

        mFabExitApp = findViewById(R.id.fab_exit_app)
        mFabExitApp?.setOnClickListener { view ->
            Snackbar.make(view, R.string.msg_exit_app_confirm, Snackbar.LENGTH_LONG)
                    .setAction(R.string.btn_msg_yes) { finish() }.show()
        }
    }

    override fun onBackPressed() {
        if (null != mWebView) {
            if (true == mWebView?.canGoBack()) {
                mWebView?.goBack()
                return
            }
        }

        super.onBackPressed()
    }

    companion object {

        private const val LOG_TAG = "MainActivity"

        @JvmStatic
        private var HOME_URL = "https://nonojapan.com"
//        private val USER_AGENT = "Mozilla/5.0 (Linux; Android 4.4; Nexus 4 Build/KRT16H) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36";
    }
}
