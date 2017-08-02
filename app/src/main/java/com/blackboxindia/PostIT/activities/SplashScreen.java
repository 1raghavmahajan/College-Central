package com.blackboxindia.PostIT.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.blackboxindia.PostIT.R;
import com.blackboxindia.PostIT.dataModels.UserInfo;

public class SplashScreen extends AppCompatActivity {

    private static final String TAG  = SplashScreen.class.getSimpleName()+" YOYO";
    public static int SPLASH_DELAY = 1000;
    public static final String ARG_IsCached = "ARG_IsCached";
    public static final String ARG_User = "ARG_user";
    public static final String ARG_Error = "ARG_error";

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        context = SplashScreen.this;

        final UserInfo userInfo = UserInfo.readCachedUserDetails(context);

        if(userInfo!=null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(context, MainActivity.class);
                    i.putExtra(ARG_User, userInfo);
                    i.putExtra(ARG_IsCached, true);
                    startActivity(i);
                    finish();
                }
            }, SPLASH_DELAY);
        }else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(context, MainActivity.class);
                    i.putExtra(ARG_IsCached, false);
                    startActivity(i);
                    finish();
                }
            }, SPLASH_DELAY);
        }

//        final UserCred userCred = new UserCred();
//        if(userCred.load_Cred(context)) {
//            NetworkMethods methods = new NetworkMethods(context);
//            methods.Login(userCred.getEmail(), userCred.getpwd(), new onLoginListener() {
//                @Override
//                public void onSuccess(UserInfo userInfo) {
//                    Log.i(TAG, "onSuccess: ");
//                    Intent i = new Intent(context,MainActivity.class);
//                    i.putExtra(ARG_User,userInfo);
//                    i.putExtra(ARG_IsCached,true);
//                    startActivity(i);
//                    finish();
//                }
//
//                @Override
//                public void onFailure(Exception e) {
//                    Log.e(TAG, "onFailure: ",e);
//                    Intent i = new Intent(context,MainActivity.class);
//                    i.putExtra(ARG_IsCached,false);
//                    i.putExtra(ARG_Error,e);
//                    startActivity(i);
//                    finish();
//                }
//            });
//        }
//        else {
//            Log.i(TAG, "onCreate: not saved");
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Intent i = new Intent(context,MainActivity.class);
//                    i.putExtra(ARG_IsCached,false);
//                    startActivity(i);
//                    finish();
//                }
//            }, SPLASH_DELAY);
//        }

    }

}
