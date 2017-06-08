package com.blackboxindia.TakeIT.Network;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

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
import com.google.firebase.storage.FirebaseStorage;

public class NetworkMethods {

    //region Variables

    private final static String TAG = NetworkMethods.class.getSimpleName() + "YOYO";

    private FirebaseAuth mAuth;
    private onLoginResultListener loginListener;

    private Context context;

    //endregion

    //region Constructors

    public NetworkMethods(Context context, FirebaseAuth auth) {
        this.context = context;
        mAuth = auth;
    }

    public NetworkMethods(Context context, onLoginResultListener i) {
        this.context = context;
        loginListener = i;
        mAuth = FirebaseAuth.getInstance();
    }

    public NetworkMethods(Context context, onLoginResultListener i, FirebaseAuth auth) {
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
                            loginListener.onFailure(task.getException());
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

    public void createNewAd(UserInfo userInfo, AdData adData, onCreateNewAdListener listener) {

        final DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if(mAuth==null)
        {
            listener.onFailure(new Exception("Not Logged In"));
        }
        else if(mAuth.getCurrentUser() == null)
        {
            listener.onFailure(new Exception("Not Logged In"));
        }
        else {

            String key = mDatabase.child("posts").push().getKey();
            String uID = mAuth.getCurrentUser().getUid();
            adData.setAdID(key);
            //adData.setCreatedBy(userInfo.getuID());
            userInfo.addUserAd(key);

            mDatabase.child("ads").child(key).setValue(adData);

            listener.onSuccess(adData);

        }
        //
//        Map<String, Object> postValues = post.toMap();
//
//        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put("/posts/" + key, postValues);
//        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);
//
//        mDatabase.updateChildren(childUpdates);

    }

    public void getAdDetails(AdDataMini adDataMini) {
        //Todo:
    }

    //endregion

    public void uploadPic() {

        FirebaseStorage storage = FirebaseStorage.getInstance();

    }

}
