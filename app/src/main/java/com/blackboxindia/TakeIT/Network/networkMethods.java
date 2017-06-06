package com.blackboxindia.TakeIT.Network;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.blackboxindia.TakeIT.dataModels.AdData;
import com.blackboxindia.TakeIT.dataModels.AdDataMini;
import com.blackboxindia.TakeIT.dataModels.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class networkMethods {

    //region Variables

    private final static String TAG = networkMethods.class.getSimpleName() + "YOYO";

    private FirebaseAuth mAuth;
    private onResultListener loginListener;

    private Context context;

    //endregion

    //region Constructors
    public networkMethods(Context context, onResultListener i) {
        this.context = context;
        loginListener = i;
        mAuth = FirebaseAuth.getInstance();
    }

    public networkMethods(Context context, onResultListener i, FirebaseAuth auth) {
        this.context = context;
        loginListener = i;
        mAuth = auth;
    }
    //endregion

    //region User Related

    public void Create_Account(final UserInfo userInfo, String password) {

        mAuth.createUserWithEmailAndPassword(userInfo.getEmail(), password)
                .addOnCompleteListener((Activity)context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.i(TAG, "Create Account Successful: " + userInfo.toString());
                            addDetailsToDB(userInfo);
                            loginListener.onSuccess(mAuth, userInfo);

                        } else {

                            Log.w(TAG, "Create Account Failure: ", task.getException());
                            loginListener.onFailure(task.getException());
                        }
                    }
                });
    }

    private void addDetailsToDB(UserInfo userInfo) {

        String uID = mAuth.getCurrentUser().getUid();
        userInfo.setuID(uID);

        Log.i(TAG, userInfo.toString());

        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(uID).setValue(userInfo);

    }

    public void Login(final String email, String pass) {

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            UserInfo userInfo = new UserInfo();
                            userInfo.setEmail(email);
                            userInfo.setuID(mAuth.getCurrentUser().getUid());
                            getDetailsFromDB(userInfo);
                            Log.i("YOYO", "isAuth Null: " + String.valueOf(mAuth == null));
                        } else {
                            Log.w(TAG, task.getException());
                            Toast.makeText(context, "Login failed.",
                                    Toast.LENGTH_SHORT).show();
                            loginListener.onSuccess(null, null);
                        }
                    }
                });

    }

    private void getDetailsFromDB(UserInfo userInfo) {

        final DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(userInfo.getuID()).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.i(TAG, "onDataChange");

                UserInfo nuserInfo = dataSnapshot.getValue(UserInfo.class);
                Log.i(TAG, nuserInfo.toString());

                loginListener.onSuccess(mAuth, nuserInfo);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());

                loginListener.onSuccess(null, null);

            }
        });

    }

    //endregion

    //region Ad Related

    public void createNewAd(AdData adData) {
        //Todo:
    }

    public void getAdDetails(AdDataMini adDataMini) {
        //Todo:
    }

    //endregion
}
