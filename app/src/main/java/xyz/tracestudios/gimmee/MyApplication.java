package xyz.tracestudios.gimmee;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.DisplayMetrics;

import com.facebook.FacebookSdk;

import java.util.Locale;

import timber.log.Timber;

public class MyApplication extends Application {
    public static final String PACKAGE_NAME = MyApplication.class.getPackage().getName();

    private static final String TAG = MyApplication.class.getSimpleName();


    public static String APP_VERSION = "0.0.0";
    public static String ANDROID_ID = "0000000000000000";

    private static MyApplication mInstance;

    public static synchronized MyApplication getInstance() {
        return mInstance;
    }


    /**
     * Method sets app specific language localization by selected shop.
     * Have to be called from every activity.
     *
     * @param lang language code.
     */
    public static void setAppLocale(String lang) {
        Resources res = mInstance.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(lang);
        Timber.d("Setting language: %s", lang);
        res.updateConfiguration(conf, dm);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        FacebookSdk.sdkInitialize(this);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            // TODO example of implementation custom crash reporting solution -  Crashlytics.
//            Fabric.with(this, new Crashlytics());
//            Timber.plant(new CrashReportingTree());
        }


        try {
            ANDROID_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            if (ANDROID_ID == null || ANDROID_ID.isEmpty()) {
                ANDROID_ID = "0000000000000000";
            }
        } catch (Exception e) {
            ANDROID_ID = "0000000000000000";
        }
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            APP_VERSION = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            Timber.e(e, "App versionName not found. WTF?. This should never happen.");
        }
    }

    /**
     * Method check, if internet is available.
     *
     * @return true if internet is available. Else otherwise.
     */
    public boolean isDataConnected() {
        ConnectivityManager connectMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectMan.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public boolean isWiFiConnection() {
        ConnectivityManager connectMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectMan.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }
}