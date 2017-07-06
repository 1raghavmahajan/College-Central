package com.blackboxindia.TakeIT.Fragments;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blackboxindia.TakeIT.Network.Interfaces.onDeleteUserListener;
import com.blackboxindia.TakeIT.Network.NetworkMethods;
import com.blackboxindia.TakeIT.R;
import com.blackboxindia.TakeIT.activities.MainActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

public class Frag_Manage extends Fragment {

    private static final int ANIMATION_DURATION = 300;

    View view;
    Context context;

    Button btn_ChangePass;
    TextView Open_ChangePass;
    TextView btn_delete;
    LinearLayout linearLayout;
    ImageView ic_right;
    TextInputEditText et_Current, et_New, et_New2;

    Boolean opened;
    int ActualHeight;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.frag_manage,container,false);
        context = view.getContext();

        opened = false;

        initVariables();

        Open_ChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openIt();
            }
        });
        btn_ChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPasswordValid())
                    changePass();
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAccount();
            }
        });

        return view;
    }

    private void deleteAccount() {
        new AlertDialog.Builder(context)
                .setIcon(R.drawable.ic_error)
                .setTitle("Delete Account")
                .setMessage("All your ads/data will be deleted, are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        NetworkMethods networkMethods = new NetworkMethods(context);
                        final ProgressDialog show = ProgressDialog.show(context, "Deleting Account", "Please wait...", true, false);
                        networkMethods.deleteUser(((MainActivity) context).userInfo, new onDeleteUserListener() {
                            @Override
                            public void onSuccess() {
                                show.cancel();
                                dialog.cancel();

                            }

                            @Override
                            public void onFailure(Exception e) {
                                show.cancel();
                                dialog.cancel();
                                new AlertDialog.Builder(context)
                                        .setTitle("Error!")
                                        .setMessage(e.getMessage())
                                        .setCancelable(true)
                                        .create().show();
                            }
                        });
                    }
                })
                .setNegativeButton("No", null)
                .setCancelable(true)
                .create().show();
    }

    private void initVariables() {
        Open_ChangePass = (TextView) view.findViewById(R.id.btn_OpenChangePass);
        ic_right = (ImageView) view.findViewById(R.id.card_open_icon);
        linearLayout = (LinearLayout) view.findViewById(R.id.other_stuff);
        btn_ChangePass = (Button) view.findViewById(R.id.btn_ChangePass);
        btn_delete = (TextView) view.findViewById(R.id.btn_DeleteAccount);

        linearLayout.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ActualHeight = linearLayout.getMeasuredHeight();

        et_Current = (TextInputEditText) view.findViewById(R.id.currentPassword);
        et_New = (TextInputEditText) view.findViewById(R.id.newPassword);
        et_New2 = (TextInputEditText) view.findViewById(R.id.conf_newPassword);
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
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null){

            //noinspection ConstantConditions
            mAuth.getCurrentUser()
                    .reauthenticate(
                            EmailAuthProvider.getCredential(
                                    mAuth.getCurrentUser().getEmail(),
                                    et_Current.getText().toString().trim()))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mAuth.getCurrentUser().updatePassword(et_New.getText().toString().trim())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        ((MainActivity)context).createSnackbar("Password Changed Successfully!", Snackbar.LENGTH_LONG);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    boolean isPasswordValid() {

        String password = et_New.getText().toString().trim();
        String cPassword = et_New2.getText().toString().trim();

        if(!password.equals(cPassword))
        {
            et_New2.setError(getString(R.string.pass_dont_match));
            return false;
        }
        else if(password.length()<getResources().getInteger(R.integer.Min_Password_Size))
        {
            et_New.setError(String.format(getString(R.string.pass_min_size),getResources().getInteger(R.integer.Min_Password_Size)));
            return false;
        }
        else if (password.contains("\"") || password.contains("\\") || password.contains("\'") || password.contains(";"))
        {
            et_New.setError(getString(R.string.pass_illegal_char));
            return false;
        }
        else
            return true;
    }

}
