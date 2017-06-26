package com.blackboxindia.TakeIT.Fragments;

import android.animation.ValueAnimator;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.activities.MainActivity;

public class frag_Manage extends Fragment {

    private static final int ANIMATION_DURATION = 300;

    View view;
    Context context;

    Button btn_ChangePass;
    LinearLayout linearLayout;

    Boolean opened;
    int ActualHeight;

    @Override
    public void onResume() {
        ((MainActivity)getActivity()).hideIT();
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_manage,container,false);
        context = view.getContext();

        opened = false;

        btn_ChangePass = (Button) view.findViewById(R.id.btn_OpenChangePass);
        linearLayout = (LinearLayout) view.findViewById(R.id.other_stuff);

        linearLayout.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ActualHeight = linearLayout.getMeasuredHeight();

        btn_ChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openIt();
            }
        });

        return view;
    }

    private void openIt() {
        if(!opened) {
            ValueAnimator anim = ValueAnimator.ofInt(0, ActualHeight);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = linearLayout.getLayoutParams();
                    layoutParams.height = val;
                    linearLayout.setLayoutParams(layoutParams);
                }
            });
            anim.setDuration(ANIMATION_DURATION);
            anim.start();
            opened = true;
        }
        else{
            ValueAnimator anim = ValueAnimator.ofInt(ActualHeight, 0);
            anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = linearLayout.getLayoutParams();
                    layoutParams.height = val;
                    linearLayout.setLayoutParams(layoutParams);
                }
            });
            anim.setDuration(ANIMATION_DURATION);
            anim.start();
            opened = false;
        }
    }

}
