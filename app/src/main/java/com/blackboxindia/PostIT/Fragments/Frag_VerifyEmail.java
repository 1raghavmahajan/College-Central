package com.blackboxindia.PostIT.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blackboxindia.PostIT.Network.Interfaces.onLoginListener;
import com.blackboxindia.PostIT.R;
import com.blackboxindia.PostIT.activities.MainActivity;
import com.blackboxindia.PostIT.dataModels.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Frag_VerifyEmail extends Fragment {

    //region Variables

    private static String TAG = Frag_VerifyEmail.class.getSimpleName()+" YOYO";
    View view;
    Context context;
    public onLoginListener loginListener;
    boolean verified;
    FirebaseUser user;
    UserInfo userInfo;

    ProgressBar progressBar;
    TextView textView;
    //endregion

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
                progressBar.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
                user.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        progressBar.setVisibility(View.INVISIBLE);
                        textView.setVisibility(View.INVISIBLE);

                        if (user != null) {
                            if (user.isEmailVerified()) {
                                Log.i(TAG, "Verified");
                                Toast.makeText(context, "Email Verified!", Toast.LENGTH_SHORT).show();
                                ((MainActivity)context).launchOtherFragment(new Frag_Main(),MainActivity.MAIN_SCREEN_TAG);
                                loginListener.onSuccess(userInfo);
                            }
                            else {
                                Toast.makeText(context, "Not Verified. Please Retry.", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
            }
        });

        return view;
    }


}
