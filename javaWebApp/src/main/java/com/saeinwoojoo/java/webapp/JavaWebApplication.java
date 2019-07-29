package com.saeinwoojoo.java.webapp;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import com.saeinwoojoo.android.thememanager.library.PackageChangedReceiver;
import com.saeinwoojoo.android.thememanager.library.ThemeManager;

import java.util.ArrayList;
import java.util.List;

public class JavaWebApplication extends Application {

    private static final String LOG_TAG = "JavaWebApplication";

    private BroadcastReceiver mBroadcastReceiver;

    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects (excluding content providers) have been created.
     *
     * <p>Implementations should be as quick as possible (for example using
     * lazy initialization of state) since the time spent in this function
     * directly impacts the performance of starting the first activity,
     * service, or receiver in a process.</p>
     *
     * <p>If you override this method, be sure to call {@code super.onCreate()}.</p>
     *
     * <p class="note">Be aware that direct boot may also affect callback order on
     * Android {@link Build.VERSION_CODES#N} and later devices.
     * Until the user unlocks the device, only direct boot aware components are
     * allowed to run. You should consider that all direct boot unaware
     * components, including such {@link ContentProvider}, are
     * disabled until user unlock happens, especially when component callback
     * order matters.</p>
     */
    @Override
    public void onCreate() {
        super.onCreate();

        List<String> resourceApkPkgNameList = new ArrayList<>();
        resourceApkPkgNameList.add(getString(R.string.resource_pkg_name_global));

        ThemeManager.initialize(this, resourceApkPkgNameList);

        mBroadcastReceiver = new PackageChangedReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED);
        intentFilter.addDataScheme("package");
        registerReceiver(mBroadcastReceiver, intentFilter);
    }

    /**
     * This method is for use in emulated process environments.  It will
     * never be called on a production Android device, where processes are
     * removed by simply killing them; no user code (including this callback)
     * is executed when doing so.
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        unregisterReceiver(mBroadcastReceiver);
    }
}
