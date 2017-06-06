package com.blackboxindia.TakeIT.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.activities.MainActivity;
import com.blackboxindia.TakeIT.dataModels.UserInfo;

public class frag_loginPage extends Fragment {

    //region Variables

    TextInputLayout inputLayoutID, inputLayoutPassword;
    EditText etID, etPassword;
    Button btn_login;
    TextView tvCreateNew;
    View view;

    //endregion

    //region Initial setup

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_login, container, false);

        NestedScrollView nestedScrollView = (NestedScrollView) view.findViewById(R.id.profile_main);
        nestedScrollView.setNestedScrollingEnabled(false);

        initVariables();

        setListeners();

        return view;
    }

    private void initVariables() {
        inputLayoutID = (TextInputLayout) view.findViewById(R.id.login_IDFrame);
        inputLayoutPassword = (TextInputLayout) view.findViewById(R.id.login_PasswordFrame);

        etID = (EditText) view.findViewById(R.id.login_etID);
        etPassword = (EditText) view.findViewById(R.id.login_etPassword);
        btn_login = (Button) view.findViewById(R.id.login_btnLogin);

        tvCreateNew = (TextView) view.findViewById(R.id.login_tvCreate);
    }

    private void setListeners() {
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndLogin(v);
            }
        });

        tvCreateNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity)v.getContext();
                mainActivity.launchOtherFragment(new frag_newAccount(), "NEW_ACCOUNT");
            }
        });

        etID.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                inputLayoutID.setErrorEnabled(false);
            }
        });

        etPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                inputLayoutPassword.setErrorEnabled(false);
            }
        });
    }

    //endregion

    public void validateAndLogin(View v) {
        String id = etID.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        UserInfo userInfo = new UserInfo();
        if (userInfo.isIDValid(id) && isPasswordValid(password))
        {
            userInfo.login(id, password, v.getContext());
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

}
