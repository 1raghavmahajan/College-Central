package com.blackboxindia.TakeIT.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.activities.MainActivity;
import com.blackboxindia.TakeIT.dataModels.UserInfo;

public class frag_loginPage extends Fragment {

    //region Variables

    private static String TAG = frag_loginPage.class.getSimpleName() + " YOYO";
    TextInputEditText etID, etPassword;
    Button btn_login;
    CheckBox chkSave;
    TextView tvCreateNew;
    View view;

    //endregion

    //region Initial setup

    @Override
    public void onResume() {
        ((MainActivity)getActivity()).hideIT();
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_login, container, false);

        initVariables();

        setListeners();

        return view;
    }

    private void initVariables() {

        etID = (TextInputEditText) view.findViewById(R.id.login_etID);
        etPassword = (TextInputEditText) view.findViewById(R.id.login_etPassword);
        btn_login = (Button) view.findViewById(R.id.login_btnLogin);

        chkSave = (CheckBox) view.findViewById(R.id.login_chkSave);

        tvCreateNew = (TextView) view.findViewById(R.id.login_tvCreate);

        etID.requestFocus();
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
                mainActivity.launchOtherFragment(new frag_newAccount(), MainActivity.NEW_ACCOUNT_TAG);
            }
        });
    }

    //endregion

    public void validateAndLogin(View v) {
        String id = etID.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        UserInfo userInfo = new UserInfo();
        if (isIDValid(id))
            if(isPasswordValid(password))
                userInfo.login(id, password, v.getContext(),chkSave.isChecked());
    }

    private boolean isIDValid(String id) {
        Boolean valid = Patterns.EMAIL_ADDRESS.matcher(id).matches();
        if(valid)
            return true;
        else {
            etID.setError("Invalid ID");
            return false;
        }
    }

    private boolean isPasswordValid(String password) {
        int Min_Password_Size = getResources().getInteger(R.integer.Min_Password_Size);
        if(password.length()<Min_Password_Size)
        {
            etPassword.setError(String.format(getString(R.string.pass_min_size),Min_Password_Size));
            return false;
        }
        else if (password.contains("\"") || password.contains("\\") || password.contains("\'") || password.contains(";"))
        {
            etPassword.setError("Password can\'t contain \", \\, \', or ;");
            return false;
        }
        else
            return true;
    }

}
