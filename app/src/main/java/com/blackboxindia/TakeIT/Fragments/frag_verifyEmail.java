package com.blackboxindia.TakeIT.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blackboxindia.TakeIT.Network.Interfaces.onLoginListener;
import com.blackboxindia.TakeIT.R;

/**
 * Created by Raghav on 22-Jun-17.
 */

public class frag_verifyEmail extends Fragment {

    View view;
    Context context;
    public onLoginListener loginListener;

    public static frag_verifyEmail newInstance(onLoginListener loginListener) {

        frag_verifyEmail fragment = new frag_verifyEmail();
        fragment.loginListener = loginListener;

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.frag_verify_email, container, false);
        context = view.getContext();

        return view;
    }



}
