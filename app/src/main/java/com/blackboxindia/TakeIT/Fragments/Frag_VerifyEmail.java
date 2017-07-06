package com.blackboxindia.TakeIT.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blackboxindia.TakeIT.Network.Interfaces.onLoginListener;
import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.activities.MainActivity;
import com.blackboxindia.TakeIT.dataModels.UserInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Frag_VerifyEmail extends Fragment {

    private static String TAG = Frag_VerifyEmail.class.getSimpleName()+" YOYO";
    View view;
    Context context;
    public onLoginListener loginListener;
    boolean verified;
    FirebaseUser user;
    UserInfo userInfo;

    ProgressBar progressBar;
    TextView textView;


    public static Frag_VerifyEmail newInstance(onLoginListener loginListener, UserInfo userInfo) {

        Frag_VerifyEmail fragment = new Frag_VerifyEmail();
        fragment.loginListener = loginListener;
        fragment.userInfo = userInfo;

        return fragment;
    }

    @Override
    public void onResume() {
        verified = false;
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.frag_verify_email, container, false);
        context = view.getContext();
        user = FirebaseAuth.getInstance().getCurrentUser();

        progressBar= (ProgressBar) view.findViewById(R.id.verify_progress);
        textView = (TextView) view.findViewById(R.id.verify_tv3);

        (view.findViewById(R.id.verify_btn_Check)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.reload();

                if (user != null) {
                    if (user.isEmailVerified()) {
                        Log.i(TAG, "Verified");
                        Toast.makeText(context, "Email Verified!", Toast.LENGTH_SHORT).show();
                        //Todo:
//                        ((MainActivity)context).goToMainFragment(false,true);
                        ((MainActivity)context).launchOtherFragment(new Frag_Main(),MainActivity.MAIN_SCREEN_TAG);
                        loginListener.onSuccess(userInfo);
                    }
                    else {
                        Toast.makeText(context, "Not Verified. Please Retry.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return view;
    }


}
