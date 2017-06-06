package com.blackboxindia.TakeIT.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.dataModels.UserInfo;

public class frag_myProfile extends Fragment {

    EditText etName, etEmail, etAddress, etPhone, etPassword;
    Button btn_update;
    View view;
    Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_myprofile, container, false);
        context = view.getContext();
        // Todo: Do this for all fragments
        NestedScrollView nestedScrollView = (NestedScrollView) view.findViewById(R.id.profile_main);
        nestedScrollView.setNestedScrollingEnabled(false);

        initVariables();

        UserInfo userInfo = getArguments().getParcelable("UserInfo");

        if (userInfo != null)
            populateViews(userInfo);

        return view;
    }

    private void initVariables() {
        etName = (EditText) view.findViewById(R.id.profile_etName);
        etEmail = (EditText) view.findViewById(R.id.profile_etEmail);
        etAddress = (EditText) view.findViewById(R.id.profile_etAddress);
        etPhone = (EditText) view.findViewById(R.id.profile_etPhone);
        etPassword = (EditText) view.findViewById(R.id.profile_etPassword);

        btn_update = (Button) view.findViewById(R.id.profile_btnUpdate);
    }

    void populateViews(UserInfo userInfo) {
        etName.setText(userInfo.getName());
        etEmail.setText(userInfo.getEmail());
        etPhone.setText(userInfo.getPhone());
        etAddress.setText(userInfo.getAddress());
        etPassword.setText("********");
    }

}
