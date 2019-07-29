package com.saeinwoojoo.kotlin.webapp.webview

import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.Browser
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.saeinwoojoo.android.thememanager.library.ThemeManager
import com.saeinwoojoo.kotlin.webapp.BaseActivity
import com.saeinwoojoo.kotlin.webapp.R
import java.net.URLDecoder
import java.util.regex.Pattern


/**
 * WebViewClientEx
 *
 */
class WebViewClientEx(baseActivity: BaseActivity?) : WebViewClient() {

    private var mBaseActivity: BaseActivity? = null

    // android default ProgressDialog 사용하는 경우
    private var mUseDialogProgressBar: Boolean = false
    private var mProgressDlg: ProgressDialog? = null

    init {

        if (null != baseActivity) {
            mBaseActivity = baseActivity
            mUseDialogProgressBar = ThemeManager.getInstance().getBoolean(baseActivity.applicationContext,
                    R.bool.USE_DLG_PROGRESS_BAR_IN_WEB_VIEW, baseActivity.getString(R.string.resource_pkg_name_global));
            if (mUseDialogProgressBar) {
                mProgressDlg = ProgressDialog(baseActivity)
                mProgressDlg!!.setMessage(baseActivity.resources.getText(R.string.msg_please_wait))
                mProgressDlg!!.isIndeterminate = true
                mProgressDlg!!.setCancelable(false)
            }
        }
    }

    override fun shouldOverrideUrlLoading(view: WebView, rawUrl: String): Boolean {
        var url = rawUrl
        if (!TextUtils.isEmpty(url)) {
            try {
                url = URLDecoder.decode(url, "utf-8")
            } catch (ex: Exception) {
                Log.e(LOG_TAG, "URL Decode Error", ex)
                return false
            }

            if (url.startsWith("mailto:") || url.startsWith("geo:")
                    || url.startsWith("tel:") || url.startsWith("intent:")) {
                try {
                    return startBrowsingIntent(mBaseActivity!!.application, url)
                } catch (ex: Exception) {
                    Log.e(LOG_TAG, "mBaseActivity is null", ex)
                    return false
                }
            }

            view.loadUrl(url)
            return true
        } else {
            return false
        }
    }

    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)

        if (mUseDialogProgressBar) {
            if (false == mProgressDlg?.isShowing)
                mProgressDlg?.show()
        }

        mBaseActivity?.showTitleProgressbar()
    }

    override fun onPageFinished(view: WebView, url: String) {
        super.onPageFinished(view, url)

        if (mUseDialogProgressBar) {
            if (true == mProgressDlg?.isShowing)
                mProgressDlg?.dismiss()
        }

        mBaseActivity?.hideTitleProgressbar()
    }

    override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
        super.onReceivedError(view, request, error)

        Log.e(LOG_TAG, "WebView Error: " + error.errorCode + ", " + error.description)
        mBaseActivity?.showToast("WebView Error: " + error.errorCode + ", " + error.description,
                    Toast.LENGTH_LONG, Gravity.CENTER)
    }

    private fun startBrowsingIntent(context: Context, url: String): Boolean {
        val intent: Intent
        // Perform generic parsing of the URI to turn it into an Intent.
        try {
            intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
        } catch (ex: Exception) {
            Log.w(LOG_TAG, "Bad URI $url", ex)
            return false
        }

        // Check for regular URIs that WebView supports by itself, but also
        // check if there is a specialized app that had registered itself
        // for this kind of an intent.
        val m = BROWSER_URI_SCHEMA.matcher(url)
        if (m.matches() && !isSpecializedHandlerAvailable(context, intent)) {
            return false
        }
        // Sanitize the Intent, ensuring web pages can not bypass browser
        // security (only access to BROWSABLE activities).
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        intent.component = null
        val selector = intent.selector
        if (null != selector) {
            selector.addCategory(Intent.CATEGORY_BROWSABLE)
            selector.component = null
        }

        // Pass the package name as application ID so that the intent from the
        // same application can be opened in the same tab.
        intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName())
        try {
            mBaseActivity?.startActivity(intent)
            return true
        } catch (ex: ActivityNotFoundException) {
            Log.w(LOG_TAG, "No application can handle $url")
        } catch (ex: SecurityException) {
            // This can happen if the Activity is exported="true", guarded by a permission, and sets
            // up an intent filter matching this intent. This is a valid configuration for an
            // Activity, so instead of crashing, we catch the exception and do nothing. See
            // https://crbug.com/808494 and https://crbug.com/889300.
            Log.w(LOG_TAG, "SecurityException when starting intent for $url")
        }

        return false
    }

    /**
     * Search for intent handlers that are specific to the scheme of the URL in the intent.
     */
    private fun isSpecializedHandlerAvailable(context: Context, intent: Intent): Boolean {
        val pm = context.packageManager
        val handlers = pm.queryIntentActivities(intent,
                PackageManager.GET_RESOLVED_FILTER)
        if (null == handlers || 0 == handlers.size) {
            return false
        }
        for (resolveInfo in handlers) {
            if (!isNullOrGenericHandler(resolveInfo.filter)) {
                return true
            }
        }
        return false
    }

    private fun isNullOrGenericHandler(filter: IntentFilter?): Boolean {
        return filter == null || (filter.countDataAuthorities() == 0 && filter.countDataPaths() == 0)
    }

    private fun getUrlFromIntent(intent: Intent?): String? {
        return intent?.dataString
    }

    companion object {

        private const val LOG_TAG = "WebViewClientEx"

        @JvmStatic
        private val BROWSER_URI_SCHEMA = Pattern.compile(
            "(?i)"   // switch on case insensitive matching
            + "("    // begin group for schema
            + "(?:http|https|file):\\/\\/"
            + "|(?:inline|data|about|chrome|javascript):"
            + ")"
            + "(.*)");
    }
}