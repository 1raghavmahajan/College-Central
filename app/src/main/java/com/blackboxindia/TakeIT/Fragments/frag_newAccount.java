package com.blackboxindia.TakeIT.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.dataModels.UserInfo;


public class frag_newAccount extends Fragment {

    //region Variables
    EditText etName, etPhone, etAddress, etEmail, etPassword, etConfirmPass;
    TextInputLayout nameFrame, phoneFrame, mailFrame, passFrame, cPassFrame;
    Button btnCreate;
    //endregion

    //region Initial Setup
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_newaccount,container,false);

        initVariables(view);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserInfo userInfo = new UserInfo(etName, etEmail, etAddress, etPhone);
                if (validateDetails(userInfo))
                    if (isPasswordValid())
                        userInfo.newUser(etPassword.getText().toString().trim(), v.getContext());
            }
        });

        return view;
    }

    private void initVariables(View view) {

        etName = (EditText) view.findViewById(R.id.create_etName);
        etPhone = (EditText) view.findViewById(R.id.create_etPhone);
        etAddress = (EditText) view.findViewById(R.id.create_etAddress);
        etEmail = (EditText) view.findViewById(R.id.create_etEmail);
        etPassword = (EditText) view.findViewById(R.id.create_etPassword);
        etConfirmPass= (EditText) view.findViewById(R.id.create_etPasswordConfirm);

        nameFrame = (TextInputLayout) view.findViewById(R.id.create_etNameFrame);
        phoneFrame = (TextInputLayout) view.findViewById(R.id.create_etPhoneFrame);
        mailFrame = (TextInputLayout) view.findViewById(R.id.create_etEmailFrame);
        passFrame = (TextInputLayout) view.findViewById(R.id.create_etPasswordFrame);
        cPassFrame = (TextInputLayout) view.findViewById(R.id.create_etPasswordConfirmFrame);

        btnCreate = (Button) view.findViewById(R.id.create_btnCreate);
    }

    //endregion

    boolean validateDetails(UserInfo userInfo) {
        Bundle bundle = userInfo.validateNewAccountDetails();
        return bundle.getBoolean("allGood");
    }

    boolean isPasswordValid() {

        String password = etPassword.getText().toString().trim();
        String cPassword = etConfirmPass.getText().toString().trim();

        if(!password.equals(cPassword))
        {
            cPassFrame.setError("Passwords don\'t match.");
            return false;
        }
        else if(password.length()<8)
        {
            passFrame.setError("Minimum 8 characters required.");
            return false;
        }
        else if (password.contains("\"") || password.contains("\\") || password.contains("\'") || password.contains(";"))
        {
            passFrame.setError("Password can\'t contain \", \\, \', or ;");
            return false;
        }
        else
            return true;
    }
}

