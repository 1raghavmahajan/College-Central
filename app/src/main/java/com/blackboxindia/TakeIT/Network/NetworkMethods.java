package com.blackboxindia.TakeIT.Network;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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

    public void uploadPic(Uri uri,String filename) {

        FirebaseStorage storage = FirebaseStorage.getInstance();

        StorageReference storageRef = storage.getReference();
        //Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
        Log.i("YOYO","Filename: " + filename);
        Log.i("YOYO","URI: " + uri.toString());
        StorageReference riversRef = storageRef.child("images/"+filename);
        UploadTask uploadTask = riversRef.putFile(uri);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.i("YOYO","onFailure");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Log.i("YOYO","onSuccess");
                @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Log.i("YOYO", "Link: "+downloadUrl.toString());
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                //Uri downloadUrl = taskSnapshot.getDownloadUrl();
            }
        })
        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });

    }

}
