package io.zamzam.basememara;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import io.fabric.sdk.android.Fabric;
import io.zamzam.basememara.util.DataHelper;

/**
 * Created by basem on 7/2/15.
 */
public class App extends Application {
    private static Context mContext;
    private static Tracker mTracker;

    @Override
    public void onCreate() {
        super.onCreate();

        Fabric.with(this, new Crashlytics());

        // Store context for static use
        mContext = getApplicationContext();

        // Initialize Google Analytics
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        mTracker = analytics.newTracker(mContext.getString(R.string.google_analytics_id));

        DataHelper.initDatabase();
    }

    public static Context getStaticContext() {
        return mContext;
    }

    public static Tracker getTracker() {
        return mTracker;
    }
}
