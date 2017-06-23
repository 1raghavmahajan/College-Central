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

public class frag_verifyEmail extends Fragment {

    private static String TAG = frag_verifyEmail.class.getSimpleName()+" YOYO";
    View view;
    Context context;
    public onLoginListener loginListener;
    boolean verified;
    FirebaseUser user;
    UserInfo userInfo;

    ProgressBar progressBar;
    TextView textView;


    public static frag_verifyEmail newInstance(onLoginListener loginListener, UserInfo userInfo) {

        frag_verifyEmail fragment = new frag_verifyEmail();
        fragment.loginListener = loginListener;
        fragment.userInfo = userInfo;

        return fragment;
    }

    @Override
    public void onResume() {
        ((MainActivity)getActivity()).hideIT();
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
                        ((MainActivity)context).goToMainFragment(false,true);
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
