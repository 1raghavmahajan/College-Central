package com.blackboxindia.PostIT.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.blackboxindia.PostIT.Network.Interfaces.onLoginListener;
import com.blackboxindia.PostIT.Network.NetworkMethods;
import com.blackboxindia.PostIT.R;
import com.blackboxindia.PostIT.dataModels.UserCred;
import com.blackboxindia.PostIT.dataModels.UserInfo;

public class SplashScreen extends AppCompatActivity {

    public static int SPLASH_DELAY = 1000;
    public static final String ARG_LoggedIn = "ARG_LoggedIn";
    public static final String ARG_User = "ARG_user";
    public static final String ARG_Error = "ARG_error";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        final Intent i = new Intent(getApplicationContext(),MainActivity.class);

        final UserCred userCred = new UserCred();
        if(userCred.load_Cred(getApplicationContext())) {
            NetworkMethods methods = new NetworkMethods(getApplicationContext());
            methods.Login(userCred.getEmail(), userCred.getpwd(), new onLoginListener() {
                @Override
                public void onSuccess(UserInfo userInfo) {
                    i.putExtra(ARG_User,userInfo);
                    i.putExtra(ARG_LoggedIn,true);
                    startActivity(i);
                    finish();
                }

                @Override
                public void onFailure(Exception e) {
                    i.putExtra(ARG_LoggedIn,false);
                    i.putExtra(ARG_Error,e);
                    startActivity(i);
                    finish();
                }
            });
        }
        else {
            i.putExtra(ARG_LoggedIn,false);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(i);
                    finish();
                }
            }, SPLASH_DELAY);
        }
    }
}
