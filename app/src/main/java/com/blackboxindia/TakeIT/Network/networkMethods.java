package com.blackboxindia.TakeIT.Network;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.blackboxindia.TakeIT.activities.MainActivity;
import com.blackboxindia.TakeIT.dataModels.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Raghav on 04-Jun-17.
 */

public class networkMethods {

    private final static String TAG = networkMethods.class.getSimpleName() + "YOYO";

    FirebaseAuth mAuth;
    public newInterface anInterface;

    Context context;

    public networkMethods(Context context, newInterface i) {
        this.context = context;
        anInterface = i;
    }

    public void Create_Account(final UserInfo userInfo, String password)
    {
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(userInfo.getEmail(), password)
                .addOnCompleteListener((Activity)context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                            create_account2(userInfo);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(context, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            anInterface.onResult(null);
                        }
                    }
                });
    }

    void create_account2(UserInfo userInfo)
    {
        String uID = mAuth.getCurrentUser().getUid();
        userInfo.setuID(uID);

        DatabaseReference mDatabase;
// ...
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(uID).setValue(userInfo);

        anInterface.onResult(mAuth);
    }

    public interface newInterface {
        void onResult(FirebaseAuth Auth);
    }

}
