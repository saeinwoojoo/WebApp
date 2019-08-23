package com.saeinwoojoo.kotlin.webapp

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.saeinwoojoo.android.thememanager.library.ThemeManager

/**
 * Base Activity.
 *
 */
open class BaseActivity : AppCompatActivity() {

    private var mBackKeyPressedTime: Long = 0
    private var mToast: Toast? = null
    private var mTitleProgressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val actionBar : ActionBar? = supportActionBar
        if (null != actionBar) {
            actionBar.setDisplayShowHomeEnabled(true)
            actionBar.setIcon(R.mipmap.ic_launcher)

            val color = ThemeManager.getInstance().getColor(applicationContext,
                    R.color.colorPrimary, getString(R.string.resource_pkg_name_global))
            val colorDrawable = ColorDrawable(color)
            actionBar.setBackgroundDrawable(colorDrawable)
        }

        val window = window
        if (null != window) {
            window.statusBarColor = ThemeManager.getInstance().getColor(
                    applicationContext, R.color.colorPrimaryDark,
                    getString(R.string.resource_pkg_name_global))
        }
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        mTitleProgressBar = findViewById<ProgressBar>(R.id.title_progress_bar)
    }

    fun setTitleProgressbarMax(max: Int) {
        mTitleProgressBar?.max = max
    }

    fun setTitleProgress(progress: Int) {
        Log.d(LOG_TAG, "setTitleProgress(): $progress")
        mTitleProgressBar?.progress = progress
    }

    fun hideTitleProgressbar() {
        mTitleProgressBar?.visibility = View.GONE
    }

    fun showTitleProgressbar() {
        mTitleProgressBar?.visibility = View.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        mToast?.cancel()
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() > mBackKeyPressedTime + EXIT_TIME_ALLOWED) {
            mBackKeyPressedTime = System.currentTimeMillis();
            showToast(R.string.msg_press_back_button_again_to_exit, Toast.LENGTH_SHORT)
        } else {
            super.onBackPressed()
        }
    }

    override fun startActivity(intent: Intent?) {
        if (null == intent) {
            Log.i(LOG_TAG, "startActivity(): argument intent is null.")
            return
        }

        try {
            super.startActivity(intent)
        } catch (e: NullPointerException) {
            e.printStackTrace()
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            showToast(R.string.msg_no_such_app_installed_in_the_device)
        }
    }

    override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        if (null == intent) {
            Log.i(LOG_TAG, "startActivityForResult(): argument intent is null.")
            return
        }

        try {
            super.startActivityForResult(intent, requestCode)
        } catch (e: NullPointerException) {
            e.printStackTrace()
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            showToast(R.string.msg_no_such_app_installed_in_the_device)
        }
    }

    /**
     * Make a standard toast that just contains a text view with the text from a resource.
     * Show the view or text notification for a long period of time, Toast.LENGTH_LONG
     *
     * @param resId The resource id of the string resource to use. Can be formatted text.
     */
    fun showToast(resId: Int) {
        mToast?.cancel()
        mToast = null

        try {
            mToast = Toast.makeText(applicationContext, resId, Toast.LENGTH_LONG)
            mToast?.show()
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        } catch (e: RuntimeException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Make a standard toast that just contains a text view with the text from a resource.
     *
     * Set the bottom location at which the notification should appear on the screen.
     * @see Gravity
     *
     *
     * @param resId The resource id of the string resource to use. Can be formatted text.
     * @param duration How long to display the message. Either Toast.LENGTH_SHORT or Toast.LENGTH_LONG
     */
    fun showToast(resId: Int, duration: Int) {
        mToast?.cancel()
        mToast = null

        try {
            mToast = Toast.makeText(this, resId, duration)
            mToast?.show()
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        } catch (e: RuntimeException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Make a standard toast that just contains a text view with the text from a resource.
     *
     * @param resId The resource id of the string resource to use. Can be formatted text.
     * @param duration How long to display the message. Either Toast.LENGTH_SHORT or Toast.LENGTH_LONG
     * @param gravity The location at which the notification should appear on the screen.
     * @see Gravity
     */
    fun showToast(resId: Int, duration: Int, gravity: Int) {
        mToast?.cancel()
        mToast = null

        try {
            mToast = Toast.makeText(this, resId, duration)
            mToast?.setGravity(gravity, mToast!!.xOffset, mToast!!.yOffset)
            mToast?.show()
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        } catch (e: RuntimeException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Make a standard toast that just contains a text view.
     * Show the view or text notification for a long period of time, Toast.LENGTH_LONG
     *
     * @param text The text to show. Can be formatted text.
     */
    fun showToast(text: CharSequence) {
        if (TextUtils.isEmpty(text))
            return

        mToast?.cancel()
        mToast = null

        mToast = Toast.makeText(this, text, Toast.LENGTH_LONG)
        try {
            mToast?.show()
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }
    }

    /**
     * Make a standard toast that just contains a text view.
     *
     * Set the bottom location at which the notification should appear on the screen.
     * @see Gravity
     *
     *
     * @param text The text to show. Can be formatted text.
     * @param duration How long to display the message. Either Toast.LENGTH_SHORT or Toast.LENGTH_LONG
     */
    fun showToast(text: CharSequence, duration: Int) {
        if (TextUtils.isEmpty(text))
            return

        mToast?.cancel()
        mToast = null

        mToast = Toast.makeText(this, text, duration)
        try {
            mToast?.show()
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }
    }

    /**
     * Make a standard toast that just contains a text view.
     *
     * @param text The text to show. Can be formatted text.
     * @param duration How long to display the message. Either Toast.LENGTH_SHORT or Toast.LENGTH_LONG
     * @param gravity The location at which the notification should appear on the screen.
     */
    fun showToast(text: CharSequence, duration: Int, gravity: Int) {
        if (TextUtils.isEmpty(text))
            return

        mToast?.cancel()
        mToast = null

        mToast = Toast.makeText(this, text, duration)
        mToast?.setGravity(gravity, mToast!!.xOffset, mToast!!.yOffset)
        try {
            mToast?.show()
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }
    }

    companion object {

        private const val LOG_TAG = "BaseActivity"
        private const val EXIT_TIME_ALLOWED = 2000
    }
}
