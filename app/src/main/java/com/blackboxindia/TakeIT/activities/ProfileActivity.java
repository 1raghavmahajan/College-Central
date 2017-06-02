package com.blackboxindia.TakeIT.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.blackboxindia.TakeIT.R;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }

    public void changeImage(View view) {
        Toast.makeText(this, "change Image", Toast.LENGTH_SHORT).show();
    }
}
