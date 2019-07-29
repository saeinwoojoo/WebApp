package com.saeinwoojoo.java.webapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.saeinwoojoo.android.thememanager.library.ThemeManager;
import com.saeinwoojoo.java.webapp.webview.WebChromeClientEx;
import com.saeinwoojoo.java.webapp.webview.WebViewClientEx;

/**
 * Shows the web content in the WebView.
 */
public class MainActivity extends BaseActivity {

    private static final String LOG_TAG = "MainActivity";
    private static String HOME_URL = "https://nonojapan.com";
//    private static final String USER_AGENT = "Mozilla/5.0 (Linux; Android 4.4; Nexus 4 Build/KRT16H) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/30.0.0.0 Mobile Safari/537.36";

    private WebView mWebView;
    private FloatingActionButton mFabExitApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

//        HOME_URL = getResources().getString(R.string.home_url);
        HOME_URL = ThemeManager.getInstance().getString(getApplicationContext(),
                R.string.home_url, getString(R.string.resource_pkg_name_global));

        mWebView = findViewById(R.id.web_view);
        if (null != mWebView) {
            WebSettings webSettings = mWebView.getSettings();
            if (null != webSettings) {
                webSettings.setJavaScriptEnabled(true);
                webSettings.setBuiltInZoomControls(true);
                webSettings.setDisplayZoomControls(false);
                webSettings.setSupportZoom(true);
                webSettings.setLoadWithOverviewMode(true);
                webSettings.setUseWideViewPort(true);
                webSettings.setPluginState(WebSettings.PluginState.ON);
                webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
                webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//                webSettings.setUserAgentString(USER_AGENT);
            }

            mWebView.clearCache(true);
            mWebView.setWebChromeClient(new WebChromeClientEx(this));
            mWebView.setWebViewClient(new WebViewClientEx(this));
            mWebView.loadUrl(HOME_URL);
        }

        mFabExitApp = findViewById(R.id.fab_exit_app);
        mFabExitApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, R.string.msg_exit_app_confirm, Snackbar.LENGTH_LONG)
                        .setAction(R.string.btn_msg_yes, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        }).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (null != mWebView) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
                return;
            }
        }

        super.onBackPressed();
    }
}
