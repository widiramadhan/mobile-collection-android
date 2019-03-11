package sfi.mobile.collection.services;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;

public class NewService extends JobIntentService {
    public static final int JOB_ID = 0x01;
    private static final String TAG = NewService.class.getSimpleName();

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, NewService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.e(TAG, "Service is running");
    }
}
