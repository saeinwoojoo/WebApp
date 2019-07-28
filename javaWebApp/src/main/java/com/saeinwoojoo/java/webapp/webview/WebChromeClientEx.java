package com.saeinwoojoo.java.webapp.webview;

import android.os.Message;
import android.util.Log;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.saeinwoojoo.java.webapp.BaseActivity;

/**
 * WebChromeClientEx
 *
 */
public class WebChromeClientEx extends WebChromeClient {

	private static final String LOG_TAG = "WebChromeClientEx";

	private BaseActivity mBaseActivity;

	public WebChromeClientEx(BaseActivity baseActivity) {
		super();
		mBaseActivity = baseActivity;
	}

	@Override
	public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
		Log.i(LOG_TAG, "onJsAlert()...");
		result.confirm();
		return true;
	}

	@Override
	public void onProgressChanged(WebView view, int newProgress) {
		super.onProgressChanged(view, newProgress);
//		Log.d(LOG_TAG, "onProgressChanged(): " + newProgress);

		if (null != mBaseActivity)
			mBaseActivity.setTitleProgress(newProgress);
	}
}