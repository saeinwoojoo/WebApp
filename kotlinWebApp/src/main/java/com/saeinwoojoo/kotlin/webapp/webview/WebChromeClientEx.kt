package com.saeinwoojoo.kotlin.webapp.webview

import android.util.Log
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import com.saeinwoojoo.kotlin.webapp.BaseActivity

/**
 * WebChromeClientEx
 *
 */
class WebChromeClientEx(private val mBaseActivity: BaseActivity?) : WebChromeClient() {

    override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
        Log.i(LOG_TAG, "onJsAlert()...")
        result.confirm()
        return true
    }

    override fun onProgressChanged(view: WebView, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
//        Log.d(LOG_TAG, "onProgressChanged(): $newProgress")
        mBaseActivity?.setTitleProgress(newProgress)
    }

    companion object {

        private const val LOG_TAG = "WebChromeClientEx"
    }
}