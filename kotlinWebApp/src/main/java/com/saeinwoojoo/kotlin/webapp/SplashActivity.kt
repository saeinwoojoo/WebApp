package com.saeinwoojoo.kotlin.webapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper

/**
 * Shows the splash screen.
 *
 */
class SplashActivity : BaseActivity() {

    private var mProgressMax: Int = 0
    private var mProgressCnt: Int = 0
    private var mHandler: Handler? = null
    private var mRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        mProgressMax = resources.getInteger(R.integer.SPLASH_PROGRESS_MAX)
        setTitleProgressbarMax(mProgressMax)
    }

    override fun onResume() {
        super.onResume()
        startProgress()
    }

    override fun onPause() {
        super.onPause()
        stopProgress()
    }

    private fun stopProgress() {
        if (null != mHandler && null != mRunnable) {
            mHandler?.removeCallbacks(mRunnable)
            mRunnable = null
            mHandler = null
        }
    }

    private fun startProgress() {
        if (null == mHandler)
            mHandler = Handler(Looper.myLooper())

        if (null == mRunnable) {
            mRunnable = Runnable {
                ++mProgressCnt
                setTitleProgress(mProgressCnt)
                if (mProgressMax <= mProgressCnt) {
                    val intent = Intent(this@SplashActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    return@Runnable
                }
                mHandler?.postDelayed(mRunnable, PROGRESS_UPDATE_DURATION.toLong())
            }
        }

        mHandler?.postDelayed(mRunnable, PROGRESS_UPDATE_DURATION.toLong())
    }

    companion object {

        private const val PROGRESS_UPDATE_DURATION = 20
    }
}
