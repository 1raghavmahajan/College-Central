package com.blackboxindia.PostIT.Network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.blackboxindia.PostIT.Fragments.Frag_VerifyEmail;
import com.blackboxindia.PostIT.Network.Interfaces.onCompleteListener;
import com.blackboxindia.PostIT.Network.Interfaces.onDeleteListener;
import com.blackboxindia.PostIT.Network.Interfaces.onDeleteUserListener;
import com.blackboxindia.PostIT.Network.Interfaces.onLoginListener;
import com.blackboxindia.PostIT.Network.Interfaces.onUpdateListener;
import com.blackboxindia.PostIT.activities.MainActivity;
import com.blackboxindia.PostIT.dataModels.AdData;
import com.blackboxindia.PostIT.dataModels.Directory;
import com.blackboxindia.PostIT.dataModels.UserCred;
import com.blackboxindia.PostIT.dataModels.UserInfo;
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
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("VisibleForTests")
public class NetworkMethods {

    //region Variables

    private final static String TAG = NetworkMethods.class.getSimpleName() + " YOYO";

    private final static String DIRECTORY_ADS = "ads";
    private final static String DIRECTORY_USERS = "users";
    private final static String DIRECTORY_HOSTELS = "hostels";
    private final static String DIRECTORY_COLLEGES = "colleges";
    final static String DIRECTORY_DATA = "data";

    public final static String TYPE_FOLDER = "TYPE_Folder";
        public final static String TYPE_PDF = "TYPE_PDF";

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
                                new CloudStorageMethods(context)
                                        .uploadProfileImage(
                                                FirebaseAuth.getInstance().getCurrentUser().getUid(), profileImage, new onCompleteListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void a) {

                                                        mAuth.getCurrentUser().sendEmailVerification()
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        //Log.i(TAG, "Create Account Successful: " + userInfo.toString());
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
                                                                //Log.e(TAG, "Failed to send email.", e);
                                                                mAuth.getCurrentUser().delete();
                                                            }
                                                        });

                                                    }

                                                    @Override
                                                    public void onFailure(Exception e) {
                                                        progressDialog.cancel();
                                                        //Log.e(TAG, "onFailure: profileImageUpload", e);
                                                        loginListener.onFailure(e);
                                                    }
                                                });
                            }
                            else {
                                mAuth.getCurrentUser().sendEmailVerification()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //Log.i(TAG, "Create Account Successful: " + userInfo.toString());
                                                addDetailsToDB(userInfo);
                                                UserCred userCred = new UserCred(userInfo.getEmail(), password);
                                                userCred.save_cred(context);
                                                progressDialog.cancel();
                                                ((MainActivity)context).UpdateUI(userInfo,false);
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
                                        //Log.e(TAG, "Failed to send email.", e);
                                        mAuth.getCurrentUser().delete();
                                    }
                                });
                                //Todo:
//                                addDetailsToDB(userInfo);
//                                UserCred userCred = new UserCred(userInfo.getEmail(), password);
//                                userCred.save_cred(context);
//                                progressDialog.cancel();
//                                ((MainActivity)context).UpdateUI(userInfo,true);
                            }

                        } else {
                            progressDialog.cancel();
                            //Log.w(TAG, "Create Account Failure: ", task.getException());
                            loginListener.onFailure(task.getException());
                        }
                    }
                });
    }

    @SuppressWarnings("ConstantConditions")
    private void addDetailsToDB(UserInfo userInfo) {
        //Log.i(TAG,"addDetailsToDB: in progress");

        String uID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userInfo.setuID(uID);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child(DIRECTORY_USERS).child(uID).setValue(userInfo);
        //Log.i(TAG,"addDetailsToDB: successful");
    }

    public void Login(final String email, String pass, final onLoginListener loginListener) {

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            ((MainActivity)context).offlineMode = false;
                            UserInfo userInfo = new UserInfo();
                            userInfo.setEmail(email);
                            //noinspection ConstantConditions
                            userInfo.setuID(mAuth.getCurrentUser().getUid());
                            getDetailsFromDB(userInfo, loginListener);

                        } else {
                            //Log.w(TAG, task.getException());
                            loginListener.onFailure(task.getException());
                        }
                    }
                });

    }

    private void getDetailsFromDB(UserInfo userInfo, final onLoginListener loginListener) {

        //Log.i(TAG,"getDetailsFromDB: in progress");
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child(DIRECTORY_USERS).child(userInfo.getuID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UserInfo nUserInfo = dataSnapshot.getValue(UserInfo.class);
                //Log.i(TAG,"getDetailsFromDB: successful");

                if(nUserInfo!=null)
                    nUserInfo.cacheUserDetails(context);
                loginListener.onSuccess(nUserInfo);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                //Log.i(TAG,"getDetailsFromDB: databaseError");
                loginListener.onFailure(databaseError.toException());
            }
        });

    }

    private void UpdateUser(final UserInfo userInfo, final onUpdateListener listener) {
        UpdateUser(userInfo,null, listener);
    }

    public void UpdateUser(final UserInfo userInfo, Bitmap profileImage, final onUpdateListener listener) {

        //Log.i(TAG,"UpdateUser: in progress");

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
                CloudStorageMethods cloudStorageMethods = new CloudStorageMethods(context);
                cloudStorageMethods.uploadProfileImage(userInfo.getuID(), profileImage, new onCompleteListener<Void>() {
                    @Override
                    public void onSuccess(Void a) {
                        //Log.i(TAG, "onSuccess: Profile Image Upload");
                        mDatabase.child(DIRECTORY_USERS).child(userInfo.getuID()).setValue(userInfo)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        userInfo.cacheUserDetails(context);
                                        if(userInfo.getUserAdKeys().size()>0) {
                                            updateAdCreater(userInfo.getUserAdKeys(), userInfo, new onCompleteListener<Void>(){

                                                @Override
                                                public void onSuccess(Void data) {
                                                    listener.onSuccess(userInfo);
                                                }

                                                @Override
                                                public void onFailure(Exception e) {
                                                    Log.e(TAG, "onFailure: updateAdCreater", e);
//                                                    listener.onFailure(e);
                                                    listener.onSuccess(userInfo);
                                                }
                                            });
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        //Log.i(TAG, "UpdateUser: failed", e);
                                        listener.onFailure(e);
                                    }
                                });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        //Log.e(TAG, "onFailure: profileImageUpload", e);
                        listener.onFailure(e);
                    }
                });
            }
            else {
                mDatabase.child(DIRECTORY_USERS).child(userInfo.getuID()).setValue(userInfo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //Log.i(TAG, "UpdateUser: successful");
                                userInfo.cacheUserDetails(context);
                                if(userInfo.getUserAdKeys().size()>0) {
                                    updateAdCreater(userInfo.getUserAdKeys(), userInfo, new onCompleteListener<Void>(){

                                        @Override
                                        public void onSuccess(Void data) {
                                            listener.onSuccess(userInfo);
                                        }

                                        @Override
                                        public void onFailure(Exception e) {
                                            Log.e(TAG, "onFailure: updateAdCreater", e);
//                                                    listener.onFailure(e);
                                            listener.onSuccess(userInfo);
                                        }
                                    });
                                }
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
                                    //Log.i(TAG, "UpdateUser: failed", e);
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

        if(userAdKeys.size()>0) {
            final boolean[] allDone = new boolean[userAdKeys.size()];
            for (int i = 0; i < userAdKeys.size(); i++)
                allDone[i] = false;

            for (int i = 0; i < userAdKeys.size(); i++) {

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
                                    f = f && k;
                                }
                                if (f) {
                                    listener.onSuccess();
                                }
                            }

                            @Override
                            public void onFailure(Exception e) {
                                deleteUserFlag = true;
                                while (deleteUserRetryCount < 5 && deleteUserFlag) {
                                    deleteUserRetryCount++;
                                    deleteAd(userInfo, adData, new onDeleteListener() {
                                        @Override
                                        public void onSuccess(UserInfo userInfo) {
                                            deleteUserFlag = false;
                                            allDone[finalI] = true;
                                            boolean f = true;
                                            for (boolean k : allDone) {
                                                f = f && k;
                                            }
                                            if (f) {
                                                listener.onSuccess();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Exception e) {

                                        }
                                    });
                                }
                                if (!deleteUserFlag || deleteUserRetryCount > 5)
                                    listener.onFailure(e);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                        //Log.w(TAG, "getAd: onCancelled", databaseError.toException());
                        listener.onFailure(databaseError.toException());
                    }

                });
            }
        }else
            listener.onSuccess();

    }

    //endregion

    //region Ad Related

    private Integer try_update_NewAd;
    private Boolean once_NewAd;
    public void createNewAd(final UserInfo userInfo, final AdData adData, final ArrayList<Uri> imgURIs, Bitmap major, final onCompleteListener<AdData> listener) {

        //Log.i(TAG,"createNewAd: begin");
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

            //Log.i(TAG, "createNewAd: key: "+key);

            adData.setAdID(key);
            adData.setCreatedBy(userInfo);

            final CloudStorageMethods methods = new CloudStorageMethods(context);

            if(major!=null){
                methods.uploadBitmap(key, major, new onCompleteListener<Void>() {
                    @Override
                    public void onSuccess(Void a) {

                        methods.uploadPics(imgURIs, key,progressDialog, new onCompleteListener<Void>() {

                            @Override
                            public void onSuccess(Void a) {

                                mDatabase.child(DIRECTORY_ADS).child(key).setValue(adData)
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                //Log.i(TAG,"createNewAd: onFailure",e);
                                                progressDialog.cancel();
                                                listener.onFailure(e);
                                            }
                                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        //Log.i(TAG,"createNewAd: onSuccess");
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
                                                    //Log.i(TAG,"onUpdate Retry #" + (2- try_update_NewAd));
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
                //Log.i(TAG, "createNewAd: major null");
                mDatabase.child(DIRECTORY_ADS).child(key).setValue(adData)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //Log.i(TAG, "createNewAd: onFailure", e);
                                progressDialog.cancel();
                                listener.onFailure(e);
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Log.i(TAG, "createNewAd: onSuccess");
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
                                    //Log.i(TAG, "onUpdate Retry #" + (2 - try_update_NewAd));
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

    public void getAd(String adID, final onCompleteListener<AdData> listener) {

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

                    //Log.w(TAG, "getAd: onCancelled", databaseError.toException());
                    listener.onFailure(databaseError.toException());
                }

            });
        }
    }

    public void getAllAds(Integer max_limit, final onCompleteListener<ArrayList<AdData>> listener) {

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

    public void editAd(final AdData adData, final onCompleteListener<AdData> listener) {
        editAd(adData, listener,false,null,null);
    }

    private Boolean once_EditAd;
    public void editAd(final AdData adData, final onCompleteListener<AdData> listener, boolean addedImages, Bitmap major, final ArrayList<Uri> imgURIs) {

        if(mAuth==null)
        {
            listener.onFailure(new Exception("Not Logged In"));
        }
        else if(mAuth.getCurrentUser() == null)
        {
            listener.onFailure(new Exception("Not Logged In"));
        }
        else {
            final ProgressDialog progressDialog = ProgressDialog.show(context, "Updating Ad...", "", true, false);

            final String key = adData.getAdID();

            final CloudStorageMethods methods = new CloudStorageMethods(context);

            if(addedImages && major!=null){
                methods.uploadBitmap(key, major, new onCompleteListener<Void>() {
                    @Override
                    public void onSuccess(Void a) {

                        methods.uploadPics(imgURIs, key,progressDialog, new onCompleteListener<Void>() {

                            @Override
                            public void onSuccess(Void a) {

                                mDatabase.child(DIRECTORY_ADS).child(key).setValue(adData)
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.cancel();
                                                listener.onFailure(e);
                                            }
                                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressDialog.cancel();
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
                                if(once_EditAd)
                                {
                                    once_EditAd =false;
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
            }else {
                mDatabase.child(DIRECTORY_ADS).child(key).setValue(adData)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "onFailure: editAd", e);
                                progressDialog.cancel();
                                listener.onFailure(e);
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "onSuccess: editAd");
                        progressDialog.cancel();
                        listener.onSuccess(adData);
                    }
                });
            }
        }
    }

    private int updateAdCreater_nos;
    private void updateAdCreater(final ArrayList<String> keys, final UserInfo userInfo, final onCompleteListener<Void> listener){
        if(mAuth==null)
        {
            listener.onFailure(new Exception("Not Logged In"));
        }
        else if(mAuth.getCurrentUser() == null)
        {
            listener.onFailure(new Exception("Not Logged In"));
        }
        else {
            updateAdCreater_nos = 0;
            for (String key : keys) {
                final String fKey = key;
                mDatabase.child(DIRECTORY_ADS).child(key).child("createdBy").setValue(userInfo)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                mDatabase.child(DIRECTORY_ADS).child(fKey).child("createdBy").setValue(userInfo)
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e(TAG, "onFailure: updateAdCreater key: "+fKey, e);
                                                listener.onFailure(e);
                                            }
                                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        updateAdCreater_nos++;
                                        if(updateAdCreater_nos==keys.size())
                                            listener.onSuccess(null);
                                    }
                                });
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                updateAdCreater_nos++;
                                if(updateAdCreater_nos==keys.size())
                                    listener.onSuccess(null);
                            }
                        });
            }
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
//                                    //Log.i(TAG, "onSuccess: updatedUser");
//                                }
//
//                                @Override
//                                public void onFailure(Exception e) {
//                                    //Log.e(TAG, "onFailure: deleteEvent", e);
//                                }
//                            });
//                        }
//                        else
                        //Log.i(TAG, "onSuccess: deleteEvent");
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Log.e(TAG, "onFailure: deleteEvent", e);
            }
        });
    }

    //endregion

    //region College Data

    public void getCollegeOptions(final onCompleteListener<ArrayList<String>> listener){
        //Log.i(TAG, "getCollegeOptions: called");
        mDatabase.child(DIRECTORY_COLLEGES).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.i(TAG, "onDataChange: getCollegeOptions");
                ArrayList<String> colleges;
                colleges = new ArrayList<>();
                Iterable<DataSnapshot> dataSnapshotChildren = dataSnapshot.getChildren();
                for (DataSnapshot d:
                     dataSnapshotChildren) {
                    colleges.add(d.getKey());
                }
//                colleges = dataSnapshot.getValue(new GenericTypeIndicator<ArrayList<String>>() {});
                if(colleges.size()!=0)
                    listener.onSuccess(colleges);
                else
                    listener.onSuccess(null);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure(databaseError.toException());
            }
        });
    }

    public void addNewCollege(String collegeName, final onCompleteListener<Void> listener){

        Map<String, String> college = new HashMap<>();
        //Todo: use this
        college.put("Confirmed", "TRUE");

        mDatabase.child(DIRECTORY_COLLEGES).child(collegeName).setValue(college)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listener.onSuccess(aVoid);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onFailure(e);
                    }
                });

    }

    public void getHostelOptions(String collegeName, final onCompleteListener<ArrayList<String>> listener){
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

    public void addNewHostel(ArrayList<String> hostels, String collegeName, final onCompleteListener<Void> listener){
        mDatabase.child(DIRECTORY_HOSTELS).child(collegeName).setValue(hostels)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listener.onSuccess(aVoid);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onFailure(e);
            }
        });
    }

    public void getNotificationGroups(String collegeName, final onCompleteListener<ArrayList<String>> listener){
        mDatabase.child(DIRECTORY_COLLEGES).child(collegeName).addListenerForSingleValueEvent(new ValueEventListener() {
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
    //endregion

    //region Folder Management

    public void createFile(String name, String path, String college, OnCompleteListener<Void> listener){
        Map<String, String> file = new HashMap<>();
        file.put("Type", TYPE_PDF);
        if(path.equals(""))
            mDatabase.child(DIRECTORY_DATA).child(college).child(name).setValue(file).addOnCompleteListener(listener);
        else
            mDatabase.child(DIRECTORY_DATA).child(college).child(path).child(name).setValue(file).addOnCompleteListener(listener);
    }

    public void createFolder(String name, String path, String college, OnCompleteListener<Void> listener) {
        Map<String, String> file = new HashMap<>();
        file.put("Type", TYPE_FOLDER);
        if(path.equals(""))
            mDatabase.child(DIRECTORY_DATA).child(college).child(name).setValue(file).addOnCompleteListener(listener);
        else
            mDatabase.child(DIRECTORY_DATA).child(college).child(path).child(name).setValue(file).addOnCompleteListener(listener);
    }

    public void getAllFiles(String college, final onCompleteListener<Directory> listener) {
        mDatabase.child(DIRECTORY_DATA).child(college).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Directory root = new Directory("root");
                getDet(dataSnapshot,root);
                listener.onSuccess(root);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                listener.onFailure(databaseError.toException());
            }
        });
    }

    private void getDet(DataSnapshot data, Directory current){
        for (DataSnapshot snapshot : data.getChildren()) {
            String s = (String) snapshot.child("Type").getValue();
            if(s!=null){
                if(s.equals(TYPE_PDF)){
                    current.files.add(snapshot.getKey());
                }
                else if(s.equals(TYPE_FOLDER)){
                    Directory dir = new Directory(snapshot.getKey());
                    getDet(snapshot,dir);
                    current.folders.add(dir);
                }
            }
        }
    }

    //endregion

}
