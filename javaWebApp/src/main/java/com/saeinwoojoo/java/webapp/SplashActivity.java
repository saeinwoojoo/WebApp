package com.saeinwoojoo.java.webapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

/**
 * Shows the splash screen.
 *
 */
public class SplashActivity extends BaseActivity {

    private static final int PROGRESS_UPDATE_DURATION = 20;

    private int mProgressMax;
    private int mProgressCnt;
    private Handler mHandler;
    private Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mProgressMax = getResources().getInteger(R.integer.SPLASH_PROGRESS_MAX);
        setTitleProgressbarMax(mProgressMax);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startProgress();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopProgress();
    }

    private void stopProgress() {
        if (null != mHandler && null != mRunnable) {
            mHandler.removeCallbacks(mRunnable);
            mRunnable = null;
            mHandler = null;
        }
    }

    private void startProgress() {
        if (null == mHandler)
            mHandler = new Handler(Looper.myLooper());

        if (null == mRunnable) {
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    ++mProgressCnt;
                    setTitleProgress(mProgressCnt);
                    if (mProgressMax <= mProgressCnt) {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                    }
                    mHandler.postDelayed(mRunnable, PROGRESS_UPDATE_DURATION);
                }
            };

            mHandler.postDelayed(mRunnable, PROGRESS_UPDATE_DURATION);
        }
    }
}
