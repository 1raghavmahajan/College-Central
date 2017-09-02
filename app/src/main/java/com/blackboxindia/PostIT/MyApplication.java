package com.blackboxindia.PostIT;

import android.app.Application;
import android.util.Log;

import com.onesignal.OneSignal;

public class MyApplication extends Application {

    private static final String TAG = MyApplication.class.getSimpleName()+" YOYO";

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate: ");
//        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.DEBUG, OneSignal.LOG_LEVEL.DEBUG);
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        super.onCreate();
    }
}
