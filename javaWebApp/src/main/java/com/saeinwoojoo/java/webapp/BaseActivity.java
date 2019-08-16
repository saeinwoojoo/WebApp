package com.saeinwoojoo.java.webapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

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
        if (null == mToast) {
            try {
                mToast = Toast.makeText(getApplicationContext(), resId, Toast.LENGTH_LONG);
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
                return;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        } else {
            try {
                mToast.setText(resId);
            } catch (RuntimeException e) {
                e.printStackTrace();
                return;
            }
            mToast.setDuration(Toast.LENGTH_LONG);
//			mToast.setGravity(Gravity.BOTTOM, 0, 0);
        }

        if (null != mToast)
            mToast.show();
    }

    /**
     * Make a standard toast that just contains a text view with the text from a resource.
     *
     * Set the bottom location at which the notification should appear on the screen.
     * @see android.view.Gravity
     *
     * @param resId The resource id of the string resource to use. Can be formatted text.
     * @param duration How long to display the message. Either Toast.LENGTH_SHORT or Toast.LENGTH_LONG
     */
    public void showToast(int resId, int duration) {
        if (null == mToast) {
            try {
                mToast = Toast.makeText(this, resId, duration);
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
                return;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        } else {
            try {
                mToast.setText(resId);
            } catch (RuntimeException e) {
                e.printStackTrace();
                return;
            }
            mToast.setDuration(duration);
//			mToast.setGravity(Gravity.BOTTOM, 0, 0);
        }

        if (null != mToast)
            mToast.show();
    }

    /**
     * Make a standard toast that just contains a text view with the text from a resource.
     *
     * @param resId The resource id of the string resource to use. Can be formatted text.
     * @param duration How long to display the message. Either Toast.LENGTH_SHORT or Toast.LENGTH_LONG
     * @param gravity The location at which the notification should appear on the screen.
     * @see android.view.Gravity
     */
    public void showToast(int resId, int duration, int gravity) {
        if (null == mToast) {
            try {
                mToast = Toast.makeText(this, resId, duration);
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
                return;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        } else {
            try {
                mToast.setText(resId);
            } catch (RuntimeException e) {
                e.printStackTrace();
                return;
            }
            mToast.setDuration(duration);
        }

        if (null != mToast) {
            mToast.setGravity(gravity, 0, 0);
            mToast.show();
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

        if (null == mToast) {
            mToast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        } else {
            try {
                mToast.setText(text);
            } catch (RuntimeException e) {
                e.printStackTrace();
                return;
            }
            mToast.setDuration(Toast.LENGTH_LONG);
//			mToast.setGravity(Gravity.BOTTOM, 0, 0);
        }

        if (null != mToast)
            mToast.show();
    }

    /**
     * Make a standard toast that just contains a text view.
     *
     * Set the bottom location at which the notification should appear on the screen.
     * @see android.view.Gravity
     *
     * @param text The text to show. Can be formatted text.
     * @param duration How long to display the message. Either Toast.LENGTH_SHORT or Toast.LENGTH_LONG
     */
    public void showToast(CharSequence text, int duration) {
        if (TextUtils.isEmpty(text))
            return;

        if (null == mToast) {
            mToast = Toast.makeText(this, text, duration);
        } else {
            try {
                mToast.setText(text);
            } catch (RuntimeException e) {
                e.printStackTrace();
                return;
            }
            mToast.setDuration(duration);
//			mToast.setGravity(Gravity.BOTTOM, 0, 0);
        }

        if (null != mToast)
            mToast.show();
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

        if (null == mToast) {
            mToast = Toast.makeText(this, text, duration);
        } else {
            try {
                mToast.setText(text);
            } catch (RuntimeException e) {
                e.printStackTrace();
                return;
            }
            mToast.setDuration(duration);
        }

        if (null != mToast) {
            mToast.setGravity(gravity, 0, 0);
            mToast.show();
        }
    }
}
