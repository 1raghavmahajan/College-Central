package com.blackboxindia.TakeIT.Fragments;

import android.animation.ValueAnimator;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.activities.MainActivity;

public class Frag_Manage extends Fragment {

    private static final int ANIMATION_DURATION = 300;

    View view;
    Context context;

    TextView btn_ChangePass;
    LinearLayout linearLayout;
    ImageView ic_right;

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

        btn_ChangePass = (TextView) view.findViewById(R.id.btn_OpenChangePass);
        ic_right = (ImageView) view.findViewById(R.id.card_open_icon);
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
            ValueAnimator animator = ValueAnimator.ofFloat(0,90);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float val = (float) animation.getAnimatedValue();
                    ic_right.setRotation(val);
                }
            });
            animator.setDuration(ANIMATION_DURATION);
            animator.start();
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
            ValueAnimator animator = ValueAnimator.ofFloat(90,0);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float val = (float) animation.getAnimatedValue();
                    ic_right.setRotation(val);
                }
            });
            animator.setDuration(ANIMATION_DURATION);
            animator.start();
            anim.start();
            opened = false;
        }
    }

    void changePass() {

    }

}
