package com.saeinwoojoo.java.webapp.webview;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.provider.Browser;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.saeinwoojoo.android.thememanager.library.ThemeManager;
import com.saeinwoojoo.java.webapp.BaseActivity;
import com.saeinwoojoo.java.webapp.R;

import java.net.URLDecoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * WebViewClientEx
 *
 */
public class WebViewClientEx extends WebViewClient {

    private static final String LOG_TAG = "WebViewClientEx";

    private BaseActivity mBaseActivity;

    // android default ProgressDialog 사용하는 경우
    private boolean mUseDialogProgressBar;
    private ProgressDialog mProgressDlg;

    public WebViewClientEx(BaseActivity baseActivity) {
        super();

        if (null != baseActivity) {
            mBaseActivity = baseActivity;
            mUseDialogProgressBar = ThemeManager.getInstance().getBoolean(baseActivity.getApplicationContext(),
                    R.bool.USE_DLG_PROGRESS_BAR_IN_WEB_VIEW, baseActivity.getString(R.string.resource_pkg_name_global));
            if (mUseDialogProgressBar) {
                mProgressDlg = new ProgressDialog(baseActivity);
                mProgressDlg.setMessage(baseActivity.getResources().getText(R.string.msg_please_wait));
                mProgressDlg.setIndeterminate(true);
                mProgressDlg.setCancelable(false);
            }
        }
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (!TextUtils.isEmpty(url)) {
            try {
                url = URLDecoder.decode(url, "utf-8");
            } catch (Exception ex) {
                Log.e(LOG_TAG, "URL Decode Error", ex);
                return false;
            }

            if (url.startsWith("mailto:") || url.startsWith("geo:")
                    || url.startsWith("tel:") || url.startsWith("intent:")) {
                if (null != mBaseActivity)
                    return startBrowsingIntent(mBaseActivity.getApplicationContext(), url);
            }

            view.loadUrl(url);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);

        if (mUseDialogProgressBar) {
            if (null != mProgressDlg && !mProgressDlg.isShowing())
                mProgressDlg.show();
        }

        if (null != mBaseActivity)
            mBaseActivity.showTitleProgressbar();
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        if (mUseDialogProgressBar) {
            if (null != mProgressDlg && mProgressDlg.isShowing())
                mProgressDlg.dismiss();
        }

        if (null != mBaseActivity)
            mBaseActivity.hideTitleProgressbar();
    }

    /*@Override
    public void onReceivedError(WebView view, int errorCode,
                                String description, String fallingUrl) {
        if (!TextUtils.isEmpty(description) && !TextUtils.isEmpty(fallingUrl)) {
            Log.e(LOG_TAG, description + "@" + fallingUrl);
            if (null != mBaseActivity)
                mBaseActivity.showToast("WebView Error: " + description + "@" + fallingUrl,
                        Toast.LENGTH_LONG, Gravity.CENTER);
        } else {
            Log.e(LOG_TAG, "WebView Error!");
            if (null != mBaseActivity)
                mBaseActivity.showToast("WebView Error", Toast.LENGTH_LONG, Gravity.CENTER);
        }
    }*/

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);

        Log.e(LOG_TAG, "WebView Error: " + error.getErrorCode() + ", " + error.getDescription());
        if (null != mBaseActivity)
            mBaseActivity.showToast("WebView Error: " + error.getErrorCode() + ", " + error.getDescription(),
                    Toast.LENGTH_LONG, Gravity.CENTER);
    }

    private boolean startBrowsingIntent(Context context, String url) {
        Intent intent;
        // Perform generic parsing of the URI to turn it into an Intent.
        try {
            intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
        } catch (Exception ex) {
            Log.w(LOG_TAG, "Bad URI: " + url, ex);
            return false;
        }
        // Check for regular URIs that WebView supports by itself, but also
        // check if there is a specialized app that had registered itself
        // for this kind of an intent.
        Matcher m = BROWSER_URI_SCHEMA.matcher(url);
        if (m.matches() && !isSpecializedHandlerAvailable(context, intent)) {
            return false;
        }
        // Sanitize the Intent, ensuring web pages can not bypass browser
        // security (only access to BROWSABLE activities).
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setComponent(null);
        Intent selector = intent.getSelector();
        if (null != selector) {
            selector.addCategory(Intent.CATEGORY_BROWSABLE);
            selector.setComponent(null);
        }

        // Pass the package name as application ID so that the intent from the
        // same application can be opened in the same tab.
        intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
        try {
            if (null != mBaseActivity) {
                mBaseActivity.startActivity(intent);
                return true;
            }
        } catch (ActivityNotFoundException ex) {
            Log.w(LOG_TAG, "No application can handle " + url);
        } catch (SecurityException ex) {
            // This can happen if the Activity is exported="true", guarded by a permission, and sets
            // up an intent filter matching this intent. This is a valid configuration for an
            // Activity, so instead of crashing, we catch the exception and do nothing. See
            // https://crbug.com/808494 and https://crbug.com/889300.
            Log.w(LOG_TAG, "SecurityException when starting intent for " + url);
        }
        return false;
    }

    /**
     * Search for intent handlers that are specific to the scheme of the URL in the intent.
     */
    private boolean isSpecializedHandlerAvailable(Context context, Intent intent) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> handlers = pm.queryIntentActivities(intent,
                PackageManager.GET_RESOLVED_FILTER);
        if (null == handlers || handlers.size() == 0) {
            return false;
        }
        for (ResolveInfo resolveInfo : handlers) {
            if (!isNullOrGenericHandler(resolveInfo.filter)) {
                return true;
            }
        }
        return false;
    }

    private boolean isNullOrGenericHandler(IntentFilter filter) {
        return filter == null
                || (filter.countDataAuthorities() == 0 && filter.countDataPaths() == 0);
    }

    private String getUrlFromIntent(Intent intent) {
        return null != intent ? intent.getDataString() : null;
    }

    private static final Pattern BROWSER_URI_SCHEMA = Pattern.compile(
            "(?i)"   // switch on case insensitive matching
                    + "("    // begin group for schema
                    + "(?:http|https|file):\\/\\/"
                    + "|(?:inline|data|about|chrome|javascript):"
                    + ")"
                    + "(.*)");
}