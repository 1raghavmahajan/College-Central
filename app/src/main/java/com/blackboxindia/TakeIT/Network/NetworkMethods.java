package com.blackboxindia.TakeIT.Network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.blackboxindia.TakeIT.Fragments.Frag_VerifyEmail;
import com.blackboxindia.TakeIT.Network.Interfaces.BitmapUploadListener;
import com.blackboxindia.TakeIT.Network.Interfaces.KeepTrackMain;
import com.blackboxindia.TakeIT.Network.Interfaces.addCollegeDataListener;
import com.blackboxindia.TakeIT.Network.Interfaces.getAllAdsListener;
import com.blackboxindia.TakeIT.Network.Interfaces.getCollegeDataListener;
import com.blackboxindia.TakeIT.Network.Interfaces.newAdListener;
import com.blackboxindia.TakeIT.Network.Interfaces.onDeleteListener;
import com.blackboxindia.TakeIT.Network.Interfaces.onDeleteUserListener;
import com.blackboxindia.TakeIT.Network.Interfaces.onLoginListener;
import com.blackboxindia.TakeIT.Network.Interfaces.onUpdateListener;
import com.blackboxindia.TakeIT.activities.MainActivity;
import com.blackboxindia.TakeIT.dataModels.AdData;
import com.blackboxindia.TakeIT.dataModels.UserCred;
import com.blackboxindia.TakeIT.dataModels.UserInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Collections;

@SuppressWarnings("VisibleForTests")
public class NetworkMethods {

    //region Variables

    private final static String TAG = NetworkMethods.class.getSimpleName() + " YOYO";

    private final static String DIRECTORY_ADS = "ads";
    private final static String DIRECTORY_USERS = "users";
    private final static String DIRECTORY_HOSTELS = "hostels";
    private final static String DIRECTORY_COLLEGES = "colleges";

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private Context context;

    //endregion

    //region Constructors

    public NetworkMethods(Context context) {
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    //endregion

    //region User Related


    public void Create_Account(final UserInfo userInfo, final String password, final Bitmap profileImage, final onLoginListener loginListener) {

        final ProgressDialog progressDialog = ProgressDialog.show(context, "Creating Account", "Please wait...", true, false);
        mAuth.createUserWithEmailAndPassword(userInfo.getEmail(), password)
                .addOnCompleteListener((Activity)context, new OnCompleteListener<AuthResult>() {
                    @SuppressWarnings("ConstantConditions")
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            if(userInfo.getHasProfileIMG()) {
                                new ImageStorageMethods(context)
                                        .uploadProfileImage(
                                                FirebaseAuth.getInstance().getCurrentUser().getUid(), profileImage, new BitmapUploadListener() {
                                                    @Override
                                                    public void onSuccess() {

                                                        mAuth.getCurrentUser().sendEmailVerification()
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Log.i(TAG, "Create Account Successful: " + userInfo.toString());
                                                                        addDetailsToDB(userInfo);
                                                                        UserCred userCred = new UserCred(userInfo.getEmail(), password);
                                                                        userCred.save_cred(context);
                                                                        progressDialog.cancel();
                                                                        ((MainActivity) context).launchOtherFragment(
                                                                                Frag_VerifyEmail.newInstance(loginListener, userInfo),
                                                                                MainActivity.VERIFY_EMAIL_TAG);

                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                progressDialog.cancel();
                                                                Toast.makeText(context, "Failed to create account", Toast.LENGTH_SHORT).show();
                                                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                                                Log.e(TAG, "Failed to send email.", e);
                                                                mAuth.getCurrentUser().delete();
                                                            }
                                                        });

                                                    }

                                                    @Override
                                                    public void onFailure(Exception e) {
                                                        progressDialog.cancel();
                                                        Log.e(TAG, "onFailure: profileImageUpload", e);
                                                        loginListener.onFailure(e);
                                                    }
                                                });
                            }
                            else {
                                mAuth.getCurrentUser().sendEmailVerification()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.i(TAG, "Create Account Successful: " + userInfo.toString());
                                                addDetailsToDB(userInfo);
                                                UserCred userCred = new UserCred(userInfo.getEmail(), password);
                                                userCred.save_cred(context);
                                                progressDialog.cancel();
                                                ((MainActivity) context).launchOtherFragment(
                                                        Frag_VerifyEmail.newInstance(loginListener, userInfo),
                                                        MainActivity.VERIFY_EMAIL_TAG);

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.cancel();
                                        Toast.makeText(context, "Failed to create account", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                                        Log.e(TAG, "Failed to send email.", e);
                                        mAuth.getCurrentUser().delete();
                                    }
                                });
                            }

                        } else {
                            progressDialog.cancel();
                            Log.w(TAG, "Create Account Failure: ", task.getException());
                            loginListener.onFailure(task.getException());
                        }
                    }
                });
    }

    @SuppressWarnings("ConstantConditions")
    private void addDetailsToDB(UserInfo userInfo) {
        Log.i(TAG,"addDetailsToDB: in progress");

        String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userInfo.setuID(uID);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child(DIRECTORY_USERS).child(uID).setValue(userInfo);
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
                            //noinspection ConstantConditions
                            userInfo.setuID(mAuth.getCurrentUser().getUid());
//                            UserInfo cachedUserDetails = UserInfo.getCachedUserDetails(userInfo.getuID(),context);
//                            if( cachedUserDetails != null)
//                                loginListener.onSuccess(cachedUserDetails);
//                            else
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

        mDatabase.child(DIRECTORY_USERS).child(userInfo.getuID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UserInfo nUserInfo = dataSnapshot.getValue(UserInfo.class);
                Log.i(TAG,"getDetailsFromDB: successful");

                //UserInfo.cacheUserDetails(nUserInfo,context);
                loginListener.onSuccess(nUserInfo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.i(TAG,"getDetailsFromDB: databaseError");
                loginListener.onFailure(databaseError.toException());
            }
        });

    }

    private void UpdateUser(final UserInfo userInfo, final onUpdateListener listener) {
        UpdateUser(userInfo,null, listener);
    }

    public void UpdateUser(final UserInfo userInfo, Bitmap profileImage, final onUpdateListener listener) {

        Log.i(TAG,"UpdateUser: in progress");

        if(mAuth==null) {
            listener.onFailure(new Exception("Not Logged in."));
        }
        else if (mAuth.getCurrentUser()==null) {
            listener.onFailure(new Exception("Not Logged in."));
        }
        else if (!mAuth.getCurrentUser().getUid().equals(userInfo.getuID())) {
            Logout(context);
            listener.onFailure(new Exception("Invalid Login Session."));
        }
        else {

            if(profileImage!=null){
                ImageStorageMethods imageStorageMethods = new ImageStorageMethods(context);
                imageStorageMethods.uploadProfileImage(userInfo.getuID(), profileImage, new BitmapUploadListener() {
                    @Override
                    public void onSuccess() {
                        Log.i(TAG, "onSuccess: Profile Image Upload");
                        mDatabase.child(DIRECTORY_USERS).child(userInfo.getuID()).setValue(userInfo)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.i(TAG, "UpdateUser: successful");
                                        //UserInfo.cacheUserDetails(userInfo,context);
                                        listener.onSuccess(userInfo);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i(TAG, "UpdateUser: failed", e);
                                listener.onFailure(e);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e(TAG, "onFailure: profileImageUpload", e);
                        listener.onFailure(e);
                    }
                });
            }
            else {
                mDatabase.child(DIRECTORY_USERS).child(userInfo.getuID()).setValue(userInfo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.i(TAG, "UpdateUser: successful");
                                //UserInfo.cacheUserDetails(userInfo,context);
                                listener.onSuccess(userInfo);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i(TAG, "UpdateUser: failed", e);
                        listener.onFailure(e);
                    }
                });
            }
        }
    }

    public void getUserDetails(String uID, final onLoginListener loginListener) {

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child(DIRECTORY_USERS).child(uID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UserInfo nUserInfo = dataSnapshot.getValue(UserInfo.class);
                loginListener.onSuccess(nUserInfo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                loginListener.onFailure(databaseError.toException());
            }
        });
    }

    public static void Logout(final Context context){
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseAuth.AuthStateListener stateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null) {
                    ((MainActivity) context).UpdateUIonLogout();
                    UserCred.clear_cred(context);
                    mAuth.removeAuthStateListener(this);
                }
            }
        };
        mAuth.addAuthStateListener(stateListener);
        mAuth.signOut();
    }

    public void deleteUser(final UserInfo userInfo, final onDeleteUserListener listener) {
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {

            deleteUserData(userInfo, new onDeleteUserListener() {
                @Override
                public void onSuccess() {

                    mDatabase.child(DIRECTORY_USERS).child(userInfo.getuID()).removeValue()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    UserCred userCred = new UserCred();
                                    userCred.load_Cred(context);
                                    //noinspection ConstantConditions
                                    FirebaseAuth.getInstance().getCurrentUser()
                                            .reauthenticate(EmailAuthProvider.getCredential(mAuth.getCurrentUser().getEmail(), userCred.getpwd()))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    //noinspection ConstantConditions
                                                    FirebaseAuth.getInstance().getCurrentUser().delete()
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    listener.onSuccess();
                                                                    ((MainActivity) context).UpdateUIonLogout();
                                                                    UserCred.clear_cred(context);
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            listener.onFailure(e);
                                                        }
                                                    });

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    listener.onFailure(e);
                                                }
                                            });


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.i(TAG, "UpdateUser: failed", e);
                                    listener.onFailure(e);
                                    }
                            });
                }
                @Override
                public void onFailure(Exception e) {
                    listener.onFailure(e);
                }
            });
        }
        else
            listener.onFailure(new Exception("Not Logged In!"));
    }

    private int deleteUserRetryCount = 0;
    private boolean deleteUserFlag = true;
    private void deleteUserData(final UserInfo userInfo, final onDeleteUserListener listener){
        deleteUserRetryCount = 0;

        ArrayList<String> userAdKeys = userInfo.getUserAdKeys();
        final boolean[] allDone = new boolean[userAdKeys.size()];
        for (int i = 0; i < userAdKeys.size(); i++)
            allDone[i] = false;

        for (int i=0;i<userAdKeys.size();i++) {

            final int finalI = i;
            mDatabase.child(DIRECTORY_ADS).child(userAdKeys.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final AdData adData = dataSnapshot.getValue(AdData.class);
                    deleteAd(userInfo, adData, new onDeleteListener() {
                        @Override
                        public void onSuccess(UserInfo userInfo) {
                            allDone[finalI] = true;
                            boolean f = true;
                            for (boolean k : allDone) {
                                f = f&&k;
                            }
                            if(f){
                                listener.onSuccess();
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            deleteUserFlag = true;
                            while (deleteUserRetryCount <5 && deleteUserFlag){
                                deleteUserRetryCount++;
                                deleteAd(userInfo, adData, new onDeleteListener() {
                                    @Override
                                    public void onSuccess(UserInfo userInfo) {
                                        deleteUserFlag = false;
                                        allDone[finalI] = true;
                                        boolean f = true;
                                        for (boolean k : allDone) {
                                            f = f&&k;
                                        }
                                        if(f){
                                            listener.onSuccess();
                                        }
                                    }
                                    @Override
                                    public void onFailure(Exception e) {

                                    }
                                });
                            }
                            if(!deleteUserFlag || deleteUserRetryCount>5)
                                listener.onFailure(e);
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    Log.w(TAG, "getAd: onCancelled", databaseError.toException());
                    listener.onFailure(databaseError.toException());
                }

            });
        }

    }

    //endregion

    //region Ad Related

    private Integer try_update_NewAd;
    private Boolean once_NewAd;
    public void createNewAd(final UserInfo userInfo, final AdData adData, final ArrayList<Uri> imgURIs, Bitmap major, final newAdListener listener) {

        Log.i(TAG,"createNewAd: begin");
        once_NewAd = true;
        try_update_NewAd = 5;

        if(mAuth==null)
        {
            listener.onFailure(new Exception("Not Logged In"));
        }
        else if(mAuth.getCurrentUser() == null)
        {
            listener.onFailure(new Exception("Not Logged In"));
        }
        else {
            final ProgressDialog progressDialog = ProgressDialog.show(context, "Creating Ad...", "", true, false);

            final String key = mDatabase.child(DIRECTORY_ADS).push().getKey();

            Log.i(TAG, "createNewAd: key: "+key);

            adData.setAdID(key);
            adData.setCreatedBy(userInfo);

            final ImageStorageMethods methods = new ImageStorageMethods(context);

            if(major!=null){
                methods.uploadBitmap(key, major, new BitmapUploadListener() {
                    @Override
                    public void onSuccess() {

                        methods.uploadPics(imgURIs, key,progressDialog, new KeepTrackMain() {

                            @Override
                            public void onSuccess() {

                                mDatabase.child(DIRECTORY_ADS).child(key).setValue(adData)
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.i(TAG,"createNewAd: onFailure",e);
                                                progressDialog.cancel();
                                                listener.onFailure(e);
                                            }
                                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.i(TAG,"createNewAd: onSuccess");
                                        userInfo.addUserAd(key);
                                        UpdateUser(userInfo, new onUpdateListener() {
                                            @Override
                                            public void onSuccess(UserInfo userInfo) {
                                                progressDialog.cancel();
                                                listener.onSuccess(adData);
                                            }
                                            @Override
                                            public void onFailure(Exception e) {
                                                if(try_update_NewAd >0) {
                                                    try_update_NewAd--;
                                                    Log.i(TAG,"onUpdate Retry #" + (2- try_update_NewAd));
                                                    UpdateUser(userInfo, this);
                                                }
                                                else {
                                                    progressDialog.cancel();
                                                    listener.onFailure(e);
                                                }
                                            }
                                        });
                                    }
                                });

                            }
                            @Override
                            public void onFailure(Exception e) {
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                storage.getReference().child("images/" + key + "/0s").delete();
                                for(int i=0;i<imgURIs.size();i++)
                                    storage.getReference().child("images/" + key + "/" + i).delete();
                                if(once_NewAd)
                                {
                                    once_NewAd =false;
                                    progressDialog.cancel();
                                    listener.onFailure(e);
                                }

                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        progressDialog.cancel();
                        listener.onFailure(e);
                    }
                });
            }
            else {
                Log.i(TAG, "createNewAd: major null");
                mDatabase.child(DIRECTORY_ADS).child(key).setValue(adData)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i(TAG, "createNewAd: onFailure", e);
                                progressDialog.cancel();
                                listener.onFailure(e);
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "createNewAd: onSuccess");
                        userInfo.addUserAd(key);
                        UpdateUser(userInfo, new onUpdateListener() {
                            @Override
                            public void onSuccess(UserInfo userInfo) {
                                progressDialog.cancel();
                                listener.onSuccess(adData);
                            }

                            @Override
                            public void onFailure(Exception e) {
                                if (try_update_NewAd > 0) {
                                    try_update_NewAd--;
                                    Log.i(TAG, "onUpdate Retry #" + (2 - try_update_NewAd));
                                    UpdateUser(userInfo, this);
                                } else {
                                    progressDialog.cancel();
                                    listener.onFailure(e);
                                }
                            }
                        });
                    }
                });
            }
        }
    }

    public void getAd(String adID, final newAdListener listener) {

        if(mAuth==null)
        {
            listener.onFailure(new Exception("Not Logged In"));
        }
        else if(mAuth.getCurrentUser() == null)
        {
            listener.onFailure(new Exception("Not Logged In"));
        }
        else {

            mDatabase.child(DIRECTORY_ADS).child(adID).addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    AdData adData = dataSnapshot.getValue(AdData.class);
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

    public void getAllAds(Integer max_limit, final getAllAdsListener listener) {

        if(max_limit!=0) {
            mDatabase.child(DIRECTORY_ADS).limitToLast(max_limit)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList<AdData> list = new ArrayList<>();
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                list.add(postSnapshot.getValue(AdData.class));
                            }
                            Collections.reverse(list);
                            listener.onSuccess(list);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            listener.onFailure(databaseError.toException());
                        }
                    });
        }
        else {
            mDatabase.child(DIRECTORY_ADS)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayList<AdData> list = new ArrayList<>();
                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                list.add(postSnapshot.getValue(AdData.class));
                            }
                            Collections.reverse(list);
                            listener.onSuccess(list);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            listener.onFailure(databaseError.toException());
                        }
                    });
        }
    }

    public void deleteAd(final UserInfo userInfo, final AdData adData, final onDeleteListener listener) {

        if(adData.getNumberOfImages()>0) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            storage.getReference().child("images/" + adData.getAdID() + "/0s").delete();
            for (int i = 0; i < adData.getNumberOfImages(); i++)
                storage.getReference().child("images/" + adData.getAdID() + "/" + i).delete();
        }
        mDatabase.child(DIRECTORY_ADS).child(adData.getAdID()).removeValue()
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                userInfo.removeUserAd(adData.getAdID());
                UpdateUser(userInfo, new onUpdateListener() {
                    @Override
                    public void onSuccess(UserInfo userInfo) {
                        listener.onSuccess(userInfo);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        listener.onFailure(e);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onFailure(e);
            }
        });
    }

    //endregion

    public void deleteEvent(final UserInfo userInfo, final AdData adData) {

        if(adData.getNumberOfImages()>0) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            storage.getReference().child("images/" + adData.getAdID() + "/0s").delete();
            for (int i = 0; i < adData.getNumberOfImages(); i++)
                storage.getReference().child("images/" + adData.getAdID() + "/" + i).delete();
        }
        mDatabase.child(DIRECTORY_ADS).child(adData.getAdID()).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

//                        if(adData.getCreatedBy().getuID().equals(userInfo.getuID())) {
//                            userInfo.removeUserAd(adData.getAdID());
//                            UpdateUser(userInfo, new onUpdateListener() {
//                                @Override
//                                public void onSuccess(UserInfo userInfo) {
//                                    Log.i(TAG, "onSuccess: updatedUser");
//                                }
//
//                                @Override
//                                public void onFailure(Exception e) {
//                                    Log.e(TAG, "onFailure: deleteEvent", e);
//                                }
//                            });
//                        }
//                        else
                        Log.i(TAG, "onSuccess: deleteEvent");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: deleteEvent", e);
            }
        });
    }

    //region College Data

    public void getCollegeOptions(final getCollegeDataListener listener){
        Log.i(TAG, "getCollegeOptions: called");
        mDatabase.child(DIRECTORY_COLLEGES).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i(TAG, "onDataChange: getCollegeOptions");

                ArrayList<String> colleges;
                colleges = dataSnapshot.getValue(new GenericTypeIndicator<ArrayList<String>>() {});

                if(colleges!= null) {
                    listener.onSuccess(colleges);
                }
                else
                    listener.onSuccess(null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure(databaseError.toException());
            }
        });
    }

    public void addNewCollege(ArrayList<String> colleges, final addCollegeDataListener listener){
        mDatabase.child(DIRECTORY_COLLEGES).setValue(colleges)
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    listener.onSuccess();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    listener.onFailure(e);
                }
            });
    }

    public void getHostelOptions(String collegeName, final getCollegeDataListener listener){
        mDatabase.child(DIRECTORY_HOSTELS).child(collegeName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<String> hostels;
                hostels = dataSnapshot.getValue(new GenericTypeIndicator<ArrayList<String>>() {});

                if(hostels !=null)
                    listener.onSuccess(hostels);
                else
                    listener.onSuccess(null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure(databaseError.toException());
            }
        });
    }

    public void addNewHostel(ArrayList<String> hostels, String collegeName, final addCollegeDataListener listener){
        mDatabase.child(DIRECTORY_HOSTELS).child(collegeName).setValue(hostels)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listener.onSuccess();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onFailure(e);
            }
        });
    }
    //endregion

}
