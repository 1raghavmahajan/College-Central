package com.blackboxindia.TakeIT.activities;

import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.blackboxindia.TakeIT.R;

public class LoginActivity extends AppCompatActivity {

    TextInputLayout inputLayoutID, inputLayoutPassword;
    EditText etID, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputLayoutID = (TextInputLayout) findViewById(R.id.login_IDLayout);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.login_layoutPassword);

        etID = (EditText) findViewById(R.id.login_etID);
        etPassword = (EditText) findViewById(R.id.login_etPassword);
    }

    public void validateAndLogin(View view) {
        String id = etID.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        if(isIDValid(id) && isPasswordValid(password))
        {
            //Todo: Login
        }
    }

    private boolean isPasswordValid(String password) {
        if(password.length()<8)
        {
            inputLayoutPassword.setError("Minimum 8 characters required.");
            return false;
        }
        else if (password.contains("\"") || password.contains("\\") || password.contains("\'") || password.contains(";"))
        {
            inputLayoutPassword.setError("Password can\'t contain \", \\, \', or ;");
            return false;
        }
        else
            return true;
    }

    private boolean isIDValid(String id) {
        if (id.length() < 4)
        {
            inputLayoutPassword.setError("Minimum 4 characters required.");
            return false;
        }
        else if (id.contains("\"") || id.contains("\\") || id.contains("\'") || id.contains(";"))
        {
            inputLayoutID.setError("ID can\'t contain \", \\, \', or ;");
            return false;
        }
        else if(!id.contains("@"))
        {
            inputLayoutID.setError("Not a valid email format.");
            return false;
        }
        else
        {
            return true;
        }
    }
}
