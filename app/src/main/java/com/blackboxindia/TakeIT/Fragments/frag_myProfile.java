package com.blackboxindia.TakeIT.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blackboxindia.TakeIT.R;

public class frag_myProfile extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_myprofile, container, false);

        /**
         * Todo:
         * Do this for all fragments
         */
        NestedScrollView nestedScrollView = (NestedScrollView) view.findViewById(R.id.profile_main);
        nestedScrollView.setNestedScrollingEnabled(false);

        return view;
    }
}
