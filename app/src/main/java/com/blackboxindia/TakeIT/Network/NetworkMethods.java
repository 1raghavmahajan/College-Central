package com.blackboxindia.TakeIT.Network;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

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

    public void createNewAd(final UserInfo userInfo, final AdData adData, ArrayList<Uri> imgURIs, Bitmap major, final AdListener listener) {

        Log.i(TAG,"createNewAd: begin");

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

//            final ProgressDialog progressDialog = ProgressDialog.show(context, "Uploading Files",
//                    "File #"+key.substring(key.length()-1), false, false);
//            final ProgressBar progressBar = ((MainActivity)context).progressBar;
//            progressBar.setVisibility(View.VISIBLE);
//            progressBar.setProgress(0);

            uploadBitmap(key,major);
            for(int i=0;i<imgURIs.size();i++) {
                uploadPic(imgURIs.get(i),key,i);
            }

            Task<Void> ads = mDatabase.child("ads").child(key).setValue(adData);
            ads.addOnFailureListener(new OnFailureListener() {
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



    @SuppressWarnings("VisibleForTests")
    private void uploadPic(Uri uri, String key, final int i) {

        StorageReference reference = storage.getReference().child("images/"+key+"/"+i);

        reference.putFile(uri).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.i(TAG,"uploadPic: onFailure"+i,exception);
                Toast.makeText(context, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    progressBar.setProgress(100,true);
//                } else
//                    progressBar.setProgress(100);
//
//                progressDialog.cancel();
//                progressBar.setVisibility(View.GONE);
                Log.i(TAG,"uploadPic: onSuccess"+i);
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                long p = (100*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                Log.i(TAG,"uploadPic: onProgress"+i+": " + p);

//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                    progressBar.setProgress((int) p,true);
//                }
//                else
//                    progressBar.setProgress((int) p);

            }
        });
    }

    @SuppressWarnings("VisibleForTests")
    public void uploadBitmap(String AdID, Bitmap bitmap){

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
