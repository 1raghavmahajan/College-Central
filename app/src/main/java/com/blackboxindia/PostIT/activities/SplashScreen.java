package com.blackboxindia.PostIT.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.blackboxindia.PostIT.Network.Interfaces.onLoginListener;
import com.blackboxindia.PostIT.Network.NetworkMethods;
import com.blackboxindia.PostIT.R;
import com.blackboxindia.PostIT.dataModels.UserCred;
import com.blackboxindia.PostIT.dataModels.UserInfo;

public class SplashScreen extends AppCompatActivity {

    private static final String TAG  = SplashScreen.class.getSimpleName()+" YOYO";
    public static int SPLASH_DELAY = 500;
    public static final String ARG_LoggedIn = "ARG_LoggedIn";
    public static final String ARG_User = "ARG_user";
    public static final String ARG_Error = "ARG_error";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        final UserCred userCred = new UserCred();
        if(userCred.load_Cred(SplashScreen.this)) {
            NetworkMethods methods = new NetworkMethods(SplashScreen.this);
            methods.Login(userCred.getEmail(), userCred.getpwd(), new onLoginListener() {
                @Override
                public void onSuccess(UserInfo userInfo) {
                    Log.i(TAG, "onSuccess: ");
                    Intent i = new Intent(SplashScreen.this,MainActivity.class);
                    i.putExtra(ARG_User,userInfo);
                    i.putExtra(ARG_LoggedIn,true);
                    startActivity(i);
                    finish();
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "onFailure: ",e);
                    Intent i = new Intent(SplashScreen.this,MainActivity.class);
                    i.putExtra(ARG_LoggedIn,false);
                    i.putExtra(ARG_Error,e);
                    startActivity(i);
                    finish();
                }
            });
        }
        else {
            Log.i(TAG, "onCreate: not saved");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashScreen.this,MainActivity.class);
                    i.putExtra(ARG_LoggedIn,false);
                    startActivity(i);
                    finish();
                }
            }, SPLASH_DELAY);
        }
    }
}
