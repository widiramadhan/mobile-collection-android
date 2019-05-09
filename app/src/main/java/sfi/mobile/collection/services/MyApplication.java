package sfi.mobile.collection.services;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    private static Context context;

    /* (non-Javadoc)
     * @see android.app.Application#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
