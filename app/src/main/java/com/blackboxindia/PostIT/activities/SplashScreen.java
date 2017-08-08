package com.blackboxindia.PostIT.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.blackboxindia.PostIT.R;

public class SplashScreen extends AppCompatActivity {

    private static final String TAG  = SplashScreen.class.getSimpleName()+" YOYO";
    public static int SPLASH_DELAY = 1000;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        context = SplashScreen.this;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(context, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_DELAY);

    }

}
