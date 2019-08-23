package com.saeinwoojoo.java.webapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.saeinwoojoo.android.thememanager.library.ThemeManager;

/**
 * Base Activity.
 *
 */
public class BaseActivity extends AppCompatActivity {

    private static final String LOG_TAG = "BaseActivity";
    private static final int EXIT_TIME_ALLOWED = 2000;

    private long mBackKeyPressedTime;
    private Toast mToast;
    private ProgressBar mTitleProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setIcon(R.mipmap.ic_launcher);

            int color = ThemeManager.getInstance().getColor(getApplicationContext(),
                    R.color.colorPrimary, getString(R.string.resource_pkg_name_global));
            ColorDrawable colorDrawable = new ColorDrawable(color);
            actionBar.setBackgroundDrawable(colorDrawable);
        }

        Window window = getWindow();
        if (null != window) {
            window.setStatusBarColor(ThemeManager.getInstance().getColor(
                    getApplicationContext(), R.color.colorPrimaryDark,
                    getString(R.string.resource_pkg_name_global)));
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        mTitleProgressBar = (ProgressBar) findViewById(R.id.title_progress_bar);
    }

    public void setTitleProgressbarMax(int max) {
        if (null != mTitleProgressBar)
            mTitleProgressBar.setMax(max);
    }

    public void setTitleProgress(final int progress) {
        if (null != mTitleProgressBar) {
            Log.d(LOG_TAG, "setTitleProgress(): " + progress);

//			mTitleProgressBar.setVisibility(View.VISIBLE);
            mTitleProgressBar.setProgress(progress);
        }
    }

    public void hideTitleProgressbar() {
        mTitleProgressBar.setVisibility(View.GONE);
    }

    public void showTitleProgressbar() {
        mTitleProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (null != mToast)
            mToast.cancel();
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() > mBackKeyPressedTime + EXIT_TIME_ALLOWED) {
            mBackKeyPressedTime = System.currentTimeMillis();
            showToast(R.string.msg_press_back_key_again_to_exit, Toast.LENGTH_SHORT);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void startActivity(Intent intent) {
        if (null == intent) {
            Log.i(LOG_TAG, "startActivity(): argument intent is null.");
            return;
        }

        try {
            super.startActivity(intent);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            showToast(R.string.msg_no_such_app_installed_in_the_device);
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        if (null == intent) {
            Log.i(LOG_TAG,
                    "startActivityForResult(): argument intent is null.");
            return;
        }

        try {
            super.startActivityForResult(intent, requestCode);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            showToast(R.string.msg_no_such_app_installed_in_the_device);
        }
    }

    /**
     * Make a standard toast that just contains a text view with the text from a resource.
     * Show the view or text notification for a long period of time, Toast.LENGTH_LONG
     *
     * @param resId The resource id of the string resource to use. Can be formatted text.
     */
    public void showToast(int resId) {
        if (null != mToast) {
            mToast.cancel();
            mToast = null;
        }

        try {
            mToast = Toast.makeText(getApplicationContext(), resId, Toast.LENGTH_LONG);
            if (null != mToast)
                mToast.show();
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Make a standard toast that just contains a text view with the text from a resource.
     *
     * Set the bottom location at which the notification should appear on the screen.
     * @see Gravity
     *
     * @param resId The resource id of the string resource to use. Can be formatted text.
     * @param duration How long to display the message. Either Toast.LENGTH_SHORT or Toast.LENGTH_LONG
     */
    public void showToast(int resId, int duration) {
        if (null != mToast) {
            mToast.cancel();
            mToast = null;
        }

        try {
            mToast = Toast.makeText(this, resId, duration);
            if (null != mToast)
                mToast.show();
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
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
    public void showToast(int resId, int duration, int gravity) {
        if (null != mToast) {
            mToast.cancel();
            mToast = null;
        }

        try {
            mToast = Toast.makeText(this, resId, duration);
            if (null != mToast) {
                mToast.setGravity(gravity, mToast.getXOffset(), mToast.getYOffset());
                mToast.show();
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Make a standard toast that just contains a text view.
     * Show the view or text notification for a long period of time, Toast.LENGTH_LONG
     *
     * @param text The text to show. Can be formatted text.
     */
    public void showToast(CharSequence text) {
        if (TextUtils.isEmpty(text))
            return;

        if (null != mToast) {
            mToast.cancel();
            mToast = null;
        }

        mToast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        if (null != mToast)
            try {
                mToast.show();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
    }

    /**
     * Make a standard toast that just contains a text view.
     *
     * Set the bottom location at which the notification should appear on the screen.
     * @see Gravity
     *
     * @param text The text to show. Can be formatted text.
     * @param duration How long to display the message. Either Toast.LENGTH_SHORT or Toast.LENGTH_LONG
     */
    public void showToast(CharSequence text, int duration) {
        if (TextUtils.isEmpty(text))
            return;

        if (null != mToast) {
            mToast.cancel();
            mToast = null;
        }

        mToast = Toast.makeText(this, text, duration);
        if (null != mToast)
            try {
                mToast.show();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
    }

    /**
     * Make a standard toast that just contains a text view.
     *
     * @param text The text to show. Can be formatted text.
     * @param duration How long to display the message. Either Toast.LENGTH_SHORT or Toast.LENGTH_LONG
     * @param gravity The location at which the notification should appear on the screen.
     */
    public void showToast(CharSequence text, int duration, int gravity) {
        if (TextUtils.isEmpty(text))
            return;

        if (null != mToast) {
            mToast.cancel();
            mToast = null;
        }

        mToast = Toast.makeText(this, text, duration);
        if (null != mToast) {
            mToast.setGravity(gravity, mToast.getXOffset(), mToast.getYOffset());
            try {
                mToast.show();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }
}
