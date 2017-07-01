package com.blackboxindia.TakeIT.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.activities.MainActivity;
import com.blackboxindia.TakeIT.dataModels.UserInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class frag_loginPage extends Fragment {

    //region Variables

    private static String TAG = frag_loginPage.class.getSimpleName() + " YOYO";
    TextInputEditText etID, etPassword;
    Button btn_login;
    TextView tvCreateNew, btn_forgot;
    View view;
    Context context;

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
        context = view.getContext();

        initVariables();

        setListeners();

        return view;
    }

    private void initVariables() {

        etID = (TextInputEditText) view.findViewById(R.id.login_etID);
        etPassword = (TextInputEditText) view.findViewById(R.id.login_etPassword);
        btn_login = (Button) view.findViewById(R.id.login_btnLogin);

        btn_forgot = (TextView) view.findViewById(R.id.login_forgotPassword);
        tvCreateNew = (TextView) view.findViewById(R.id.login_tvCreate);

        etID.requestFocus();
    }

    private void setListeners() {
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndLogin();
            }
        });

        tvCreateNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity)context;
                mainActivity.launchOtherFragment(new frag_newAccount(), MainActivity.NEW_ACCOUNT_TAG);
            }
        });

        btn_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(FirebaseAuth.getInstance().getCurrentUser() == null)
                {
                    if(isIDValid(etID.getText().toString().trim()))
                    {
                        new AlertDialog.Builder(context)
                                .setMessage("A password reset mail will be sent to your registered email ID.")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FirebaseAuth.getInstance()
                                                .sendPasswordResetEmail(etID.getText().toString().trim())
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        ((MainActivity)context).createSnackbar("Check email for further instructions", Snackbar.LENGTH_LONG);
                                                        Toast.makeText(context, "Email sent!", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.e(TAG,"password reset failed",e);
                                                        if(e.getMessage().contains("no user record"))
                                                            Toast.makeText(context, "No account exists with that email ID", Toast.LENGTH_SHORT).show();
                                                        else
                                                            Toast.makeText(context, "Unable to send email", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                }).setNeutralButton("Cancel", null)
                                .setCancelable(true)
                                .create()
                                .show();
                    }
                }
                else
                    Toast.makeText(context, "Already Logged In!", Toast.LENGTH_SHORT).show();


            }
        });
    }

    //endregion

    public void validateAndLogin() {
        String id = etID.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        UserInfo userInfo = new UserInfo();
        if (isIDValid(id))
            if(isPasswordValid(password))
                userInfo.login(id, password, context);
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
