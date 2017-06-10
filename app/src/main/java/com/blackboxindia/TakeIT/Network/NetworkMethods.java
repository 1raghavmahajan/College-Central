package com.blackboxindia.TakeIT.Network;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.blackboxindia.TakeIT.Network.Interfaces.AdListener;
import com.blackboxindia.TakeIT.Network.Interfaces.BitmapUploadListener;
import com.blackboxindia.TakeIT.Network.Interfaces.KeepTrackMain;
import com.blackboxindia.TakeIT.Network.Interfaces.onLoginListener;
import com.blackboxindia.TakeIT.Network.Interfaces.onUpdateListener;
import com.blackboxindia.TakeIT.dataModels.AdData;
import com.blackboxindia.TakeIT.dataModels.AdDataMini;
import com.blackboxindia.TakeIT.dataModels.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;

@SuppressWarnings("VisibleForTests")
public class NetworkMethods {

    //region Variables

    private final static String TAG = NetworkMethods.class.getSimpleName() + " YOYO";

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private Context context;

    //endregion

    //region Constructors

    public NetworkMethods(Context context) {
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
    }

    public NetworkMethods(Context context, FirebaseAuth auth) {
        this.context = context;
        mAuth = auth;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    //endregion

    //region User Related

    public void Create_Account(final UserInfo userInfo, String password, final onLoginListener loginListener) {

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
        Log.i(TAG,"addDetailsToDB: in progress");

        String uID = mAuth.getCurrentUser().getUid();
        userInfo.setuID(uID);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(uID).setValue(userInfo);
        Log.i(TAG,"addDetailsToDB: successful");
    }

    public void Login(final String email, String pass, final onLoginListener loginListener) {

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            UserInfo userInfo = new UserInfo();
                            userInfo.setEmail(email);
                            userInfo.setuID(mAuth.getCurrentUser().getUid());
                            getDetailsFromDB(userInfo, loginListener);
                        } else {
                            Log.w(TAG, task.getException());
                            loginListener.onFailure(task.getException());
                        }
                    }
                });

    }

    private void getDetailsFromDB(UserInfo userInfo, final onLoginListener loginListener) {

        Log.i(TAG,"getDetailsFromDB: in progress");
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("users").child(userInfo.getuID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UserInfo nUserInfo = dataSnapshot.getValue(UserInfo.class);
                Log.i(TAG,"getDetailsFromDB: successful");

                loginListener.onSuccess(mAuth, nUserInfo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.i(TAG,"getDetailsFromDB: databaseError");
                loginListener.onFailure(databaseError.toException());
            }
        });

//        mDatabase.child("users").child(userInfo.getuID()).addValueEventListener(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                Log.i(TAG, "onDataChange");
//
//                UserInfo nuserInfo = dataSnapshot.getValue(UserInfo.class);
//                Log.i(TAG, nuserInfo.toString());
//
//                loginListener.onSuccess(mAuth, nuserInfo);
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
//
//                loginListener.onFailure(databaseError.toException());
//
//            }
//        });

    }

    public void updateUser(final UserInfo userInfo, final onUpdateListener listener) {

        Log.i(TAG,"updateUser: in progress");

        if(mAuth==null) {
            listener.onFailure(new Exception("Not Logged in."));
        }
        else if (mAuth.getCurrentUser()==null) {
            listener.onFailure(new Exception("Not Logged in."));
        }
        else if (!mAuth.getCurrentUser().getUid().equals(userInfo.getuID())) {

            mAuth.signOut();
            listener.onFailure(new Exception("Invalid Login Session."));
        }
        else {

            mDatabase.child("users").child(userInfo.getuID()).setValue(userInfo)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i(TAG,"updateUser: successful");
                    listener.onSuccess(userInfo);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i(TAG,"updateUser: failed",e);
                    listener.onFailure(e);
                }
            });
        }
//            Map<String, Object> postValues = post.toMap();
//
//            Map<String, Object> childUpdates = new HashMap<>();
//            childUpdates.put("/posts/" + key, postValues);
//            childUpdates.put("/user-posts/" + userId + "/" + key, postValues);
//
//            mDatabase.updateChildren(childUpdates);

    }

    //endregion

    //region Ad Related

    private Boolean once;
    public void createNewAd(final UserInfo userInfo, final AdData adData, final ArrayList<Uri> imgURIs, Bitmap major, final AdListener listener) {

        Log.i(TAG,"createNewAd: begin");
        once = true;

        if(mAuth==null)
        {
            listener.onFailure(new Exception("Not Logged In"));
        }
        else if(mAuth.getCurrentUser() == null)
        {
            listener.onFailure(new Exception("Not Logged In"));
        }
        else {

            final String key = mDatabase.child("ads").push().getKey();
            String uID = mAuth.getCurrentUser().getUid();

            adData.setAdID(key);
            adData.setCreatedBy(uID);

            final CloudStorageMethods methods = new CloudStorageMethods(context,mAuth);

            methods.uploadBitmap(key, major, new BitmapUploadListener() {
                @Override
                public void onSuccess() {
                    methods.uploadPics(imgURIs, key, new KeepTrackMain() {
                        @Override
                        public void onSuccess() {

                            mDatabase.child("ads").child(key).setValue(adData)
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.i(TAG,"createNewAd: onFailure",e);
                                            listener.onFailure(e);
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.i(TAG,"createNewAd: onSuccess");
                                    userInfo.addUserAd(key);

                                    listener.onSuccess(adData);
                                }
                            });

                        }
                        @Override
                        public void onFailure(Exception e) {

                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            storage.getReference().child("images/" + key + "/0s").delete();
                            for(int i=0;i<imgURIs.size();i++)
                                storage.getReference().child("images/" + key + "/" + i).delete();
                            if(once)
                            {
                                once=false;
                                listener.onFailure(e);
                            }

                        }
                    });
                }

                @Override
                public void onFailure(Exception e) {

                }
            });
        }
    }

    private void getAd(AdDataMini adDataMini, final AdListener listener) {

        Log.i(TAG, "getAd: onDataChange");
        if(mAuth==null)
        {
            listener.onFailure(new Exception("Not Logged In"));
        }
        else if(mAuth.getCurrentUser() == null)
        {
            listener.onFailure(new Exception("Not Logged In"));
        }
        else {

            mDatabase = FirebaseDatabase.getInstance().getReference();
            //String uID = mAuth.getCurrentUser().getUid();

            mDatabase.child("ads").child(adDataMini.getAdID()).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    AdData adData = dataSnapshot.getValue(AdData.class);
                    Log.i(TAG, "getAd: onDataChange successful");
                    listener.onSuccess(adData);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    Log.w(TAG, "getAd: onCancelled", databaseError.toException());
                    listener.onFailure(databaseError.toException());
                }
            });
        }
    }

    private void getAllAds(UserInfo userInfo, Integer max_limit) {

    }

    //endregion

}
