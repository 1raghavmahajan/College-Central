package com.blackboxindia.TakeIT.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.blackboxindia.TakeIT.R;

public class frag_loginPage extends Fragment {

    TextInputLayout inputLayoutID, inputLayoutPassword;
    EditText etID, etPassword;
    Button btn_login;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_login, container, false);
        inputLayoutID = (TextInputLayout) view.findViewById(R.id.login_IDLayout);
        inputLayoutPassword = (TextInputLayout) view.findViewById(R.id.login_layoutPassword);

        etID = (EditText) view.findViewById(R.id.login_etID);
        etPassword = (EditText) view.findViewById(R.id.login_etPassword);
        btn_login = (Button) view.findViewById(R.id.login_btnLogin);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("YOYO", "onClick");
                validateAndLogin();
            }
        });

        etID.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.i("YOYO", "etID onClick");
                inputLayoutID.setErrorEnabled(false);
            }
        });

        etPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("YOYO", "etPassword onClick");
                inputLayoutPassword.setErrorEnabled(false);
            }
        });

        return view;
    }

    public void validateAndLogin() {
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
