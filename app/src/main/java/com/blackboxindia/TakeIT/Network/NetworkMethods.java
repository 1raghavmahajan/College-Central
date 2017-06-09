package com.blackboxindia.TakeIT.Network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.blackboxindia.TakeIT.activities.MainActivity;
import com.blackboxindia.TakeIT.cameraIntentHelper.BitmapHelper;
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
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@SuppressWarnings("VisibleForTests")
public class NetworkMethods {

    //region Variables

    private final static String TAG = NetworkMethods.class.getSimpleName() + " YOYO";

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseStorage storage;

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

    public void Create_Account(final UserInfo userInfo, String password, final onLoginResultListener loginListener) {

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

    public void Login(final String email, String pass, final onLoginResultListener loginListener) {

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

    private void getDetailsFromDB(UserInfo userInfo, final onLoginResultListener loginListener) {

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

    public void updateUser(UserInfo userInfo, onLoginResultListener listener) {

//        Map<String, Object> postValues = post.toMap();
//
//        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put("/posts/" + key, postValues);
//        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);
//
//        mDatabase.updateChildren(childUpdates);

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

            storage = FirebaseStorage.getInstance();

            uploadBitmap(key,major);

            uploadPics(imgURIs, key, new KeepTrackMain() {
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

    private ArrayList<Integer> progress;
    private ArrayList<Boolean> allGood;
    private ArrayList<Integer> retryNo;
    private void uploadPics(final ArrayList<Uri> imgURIs, final String key, final KeepTrackMain mainListener) {

        final ProgressDialog progressDialog = ProgressDialog.show(context, "Uploading Images", "", true, false);
        final ProgressBar progressBar = ((MainActivity)context).progressBar;
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);

        final int total = imgURIs.size();
        progress = new ArrayList<>(total);
        allGood = new ArrayList<>(total);
        retryNo = new ArrayList<>(total);
        for(int j=0;j<total;j++) {
            progress.set(j, 0);
            allGood.set(j,false);
            retryNo.set(j,0);
        }

        KeepTrack listener = new KeepTrack() {
            @Override
            public void onSuccess(int i) {
                progressBar.incrementProgressBy((100-progress.get(i))/total);
                allGood.set(i,true);
                Boolean b=true;
                for(int j=0;j<total;j++)
                    b = b && allGood.get(j);
                if(b)
                {
                    progressBar.setVisibility(View.GONE);
                    progressDialog.cancel();
                    mainListener.onSuccess();
                }
            }

            @Override
            public void failure(Exception e, int i) {
                Log.i(TAG,"failure of #"+i+" Due to " + e.getMessage());
                if(retryNo.get(i)<5) {
                    retryNo.set(i,retryNo.get(i)+1);
                    Log.i(TAG, "Retrying.. #" + retryNo.get(i));
                    uploadPic(imgURIs.get(i),key,i,this);
                }
                else {
                    //Cancel task
                    progressBar.setVisibility(View.GONE);
                    progressDialog.cancel();
                    mainListener.onFailure(e);
                }
            }

            @Override
            public void onProgressUpdate(int i, int p) {
                progress.set(i,p);
                progressBar.incrementProgressBy((p-progress.get(i))/total);
            }
        };

        for(int i=0;i<total;i++) {
            uploadPic(imgURIs.get(i),key, i, listener);
        }
    }

    private void uploadPic(Uri uri, String key, final int i, final KeepTrack listener) {

        StorageReference reference = storage.getReference().child("images/" + key + "/" + i);

        reference.putFile(uri)
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.i(TAG, "uploadPics: onFailure" + i, exception);
                    listener.failure(exception, i);
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.i(TAG, "uploadPics: onSuccess" + i);
                listener.onSuccess(i);
            }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    long p = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    Log.i(TAG, "uploadPics: onProgress" + i + ": " + p);
                    listener.onProgressUpdate(i, (int) p);
                }
            });
    }

    interface KeepTrack {

        void onSuccess(int i);
        void failure(Exception e,int i);
        void onProgressUpdate(int i, int p);

    }

    interface KeepTrackMain {
        void onSuccess();
        void onFailure(Exception e);
    }

    private void uploadBitmap(String AdID, Bitmap bitmap){

        StorageReference riversRef = storage.getReference().child("images/"+AdID+"/0s");

        riversRef.putBytes(BitmapHelper.bitmapToByteArray(bitmap))
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.i(TAG,"uploadBitmap: onFailure",exception);
                    Toast.makeText(context, exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i(TAG,"uploadBitmap: onSuccess");
                }
            });
    }

    public void getImages(String AdID) {

        storage = FirebaseStorage.getInstance();
        StorageReference islandRef = storage.getReference().child("images/"+AdID+"/0s");

        File localFile = null;
        try {
            localFile = File.createTempFile("img","jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //context.getExternalFilesDir()

        if (localFile != null) {

            islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    // Local temp file has been created
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });

        }

    }

    public void getMajorImage(String AdID, final BitmapResultListener listener) {

        storage = FirebaseStorage.getInstance();
        StorageReference islandRef = storage.getReference().child("images/"+AdID+"/0s");

        Log.i(TAG,"getMajorImage for AdID: "+ AdID);

        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.i(TAG,"getMajorImage: onSuccess");
                Bitmap bitmap = BitmapHelper.byteArrayToBitmap(bytes);
                listener.onSuccess(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.i(TAG,"getMajorImage: onFailure");
                listener.onFailure(exception);
            }
        });

    }

}
