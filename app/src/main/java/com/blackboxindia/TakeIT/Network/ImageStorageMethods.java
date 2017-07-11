package com.blackboxindia.TakeIT.Network;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.blackboxindia.TakeIT.Network.Interfaces.BitmapDownloadListener;
import com.blackboxindia.TakeIT.Network.Interfaces.BitmapUploadListener;
import com.blackboxindia.TakeIT.Network.Interfaces.ImageDownloadListener;
import com.blackboxindia.TakeIT.Network.Interfaces.KeepTrack;
import com.blackboxindia.TakeIT.Network.Interfaces.KeepTrackMain;
import com.blackboxindia.TakeIT.activities.MainActivity;
import com.blackboxindia.TakeIT.cameraIntentHelper.BitmapHelper;
import com.blackboxindia.TakeIT.cameraIntentHelper.ImageUtils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("VisibleForTests")
public class ImageStorageMethods {

    private static String TAG = ImageStorageMethods.class.getSimpleName() + " YOYO";
    private static Integer MAX_UPLOAD_RES[] = {900,900};

    private Context context;
    private FirebaseStorage storage;

    public ImageStorageMethods(Context context) {
        this.context = context;
        storage = FirebaseStorage.getInstance();
        cachedBigImages = new HashMap<>();
        cachedIcons = new HashMap<>();
        cachedProfileImages = new HashMap<>();
    }

    private ArrayList<Integer> progress;
    private ArrayList<Boolean> allGood;
    private ArrayList<Integer> retryNo;
    void uploadPics(final ArrayList<Uri> imgURIs, final String key, final ProgressDialog progressDialog, final KeepTrackMain mainListener) {
        Log.i(TAG,"uploadPics");
        progressDialog.setTitle("Uploading Images..");
        //final ProgressDialog progressDialog = ProgressDialog.show(context, "Uploading Images", "", true, false);
        final ProgressBar progressBar = ((MainActivity)context).progressBar;
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        progressBar.setMax(100*imgURIs.size());

        final int total = imgURIs.size();
        progress = new ArrayList<>(total);
        allGood = new ArrayList<>(total);
        retryNo = new ArrayList<>(total);
        for(int j=0;j<total;j++) {
            progress.add(j, 0);
            allGood.add(j,false);
            retryNo.add(j,0);
        }

        KeepTrack listener = new KeepTrack() {
            @Override
            public void onSuccess(int i) {
                progress.set(i,100);
                int tt=0;
                for(int j=0;j<total;j++)
                    tt+=progress.get(j);
                progressBar.setProgress(tt);

                allGood.set(i,true);
                Boolean b=true;
                for(int j=0;j<total;j++)
                    b = b && allGood.get(j);
                if(b)
                {
                    progressBar.setVisibility(View.GONE);
                    //progressDialog.cancel();
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
                    //progressDialog.cancel();
                    mainListener.onFailure(e);
                }
            }

            @Override
            public void onProgressUpdate(int i, int p) {
                progress.set(i,p);
                //progressBar.incrementProgressBy(p-progress.get(i));
                int tt=0;
                for(int j=0;j<total;j++)
                    tt+=progress.get(j);

                Log.i(TAG,"TOTAL Progress: " + (tt/total) + "%");
                progressBar.setProgress(tt);
            }
        };

        for(int i=0;i<total;i++) {
            uploadPic(imgURIs.get(i),key, i, listener);
        }
    }

    private void uploadPic(Uri uri, String key, final int i, final KeepTrack listener) {
        uploadPicWorker worker = new uploadPicWorker(key,i,listener);
        worker.execute(uri);
    }

    void uploadBitmap(String AdID, Bitmap bitmap, final BitmapUploadListener listener){
        Log.i(TAG,"bitmap up started");
        uploadBitmapWorker task = new uploadBitmapWorker("images/"+AdID+"/0s", listener);
        task.execute(bitmap);
    }

    private Map<String,Uri> cachedIcons;
    public void getMajorImage(final String AdID, final BitmapDownloadListener listener) {

        if(cachedIcons.containsKey(AdID)) {
            Log.i(TAG,"getMajorImage cached");
            listener.onSuccess(cachedIcons.get(AdID));
        }
        else {
            final File localFile;

            localFile = new File(context.getCacheDir(), AdID + ".webp");

            storage.getReference().child("images/" + AdID + "/0s").getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getPath());
                            cachedIcons.put(AdID,Uri.fromFile(localFile));
                            listener.onSuccess(Uri.fromFile(localFile));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            listener.onFailure(e);
                        }
                    });
        }
    }

    private Map<String,Uri> cachedBigImages;
    public void getBigImage(final String AdID, final int i, final ImageDownloadListener listener) {

        if(cachedBigImages.containsKey(AdID + i)) {
            Log.i(TAG,"Getting image from cache "+ AdID + i );
            listener.onSuccess(cachedBigImages.get(AdID + i));
        }
        else {
            Log.i(TAG,"Getting image from internet "+ AdID + i );

            final File localFile;
            localFile = new File(context.getCacheDir(), AdID + i + ".bmp");

            storage.getReference().child("images/" + AdID + "/" + i).getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Log.i(TAG,"uri:" + Uri.fromFile(localFile));
                            cachedBigImages.put(AdID+i,Uri.fromFile(localFile));
                            listener.onSuccess(Uri.fromFile(localFile));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    listener.onFailure(exception);
                }
            });

        }
    }

    void uploadProfileImage(String uID, Bitmap bitmap, final BitmapUploadListener listener){
        Log.i(TAG,"bitmap up started");
        uploadBitmapWorker task = new uploadBitmapWorker("user/"+uID+"/profileImage", listener);
        task.execute(bitmap);
    }

    private Map<String,UriNew> cachedProfileImages;
    public void getProfileImage(final String uID, final BitmapDownloadListener listener) {

        if(cachedProfileImages.containsKey(uID)) {

            storage.getReference().child("user/"+uID+"/profileImage").getMetadata()
                    .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                        @Override
                        public void onSuccess(final StorageMetadata storageMetadata) {
                            if(cachedProfileImages.get(uID).timeStamp< storageMetadata.getUpdatedTimeMillis())
                            {
                                final File localFile;
                                localFile = new File(context.getCacheDir(), uID + "_image.webp");
                                storage.getReference().child("user/"+uID+"/profileImage").getFile(localFile)
                                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                cachedProfileImages.put(uID,new UriNew(Uri.fromFile(localFile),storageMetadata.getUpdatedTimeMillis()));
                                                listener.onSuccess(Uri.fromFile(localFile));
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                                Log.e(TAG, "onFailure: getNewImage",e);
                                                listener.onSuccess(cachedProfileImages.get(uID).uri);
                                            }
                                        });
                            }
                            else {
                                listener.onSuccess(cachedProfileImages.get(uID).uri);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onFailure: getMetaData",e);
                            listener.onSuccess(cachedProfileImages.get(uID).uri);
                        }
                    });
        }
        else {

            final File localFile;

            localFile = new File(context.getCacheDir(), uID + "_image.webp");

            storage.getReference().child("user/"+uID+"/profileImage").getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            storage.getReference().child("user/"+uID+"/profileImage").getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                                @Override
                                public void onSuccess(StorageMetadata storageMetadata) {
                                    cachedProfileImages.put(uID,new UriNew(Uri.fromFile(localFile),storageMetadata.getUpdatedTimeMillis()));
                                }
                            });
                            listener.onSuccess(Uri.fromFile(localFile));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    listener.onFailure(e);
                }
            });
        }
    }

    public void saveCache(){
        Log.i(TAG,"saveCache");

        SharedPreferences cache = context.getSharedPreferences("cache", Context.MODE_PRIVATE);

        SharedPreferences.Editor edit = cache.edit();

        edit.putBoolean("isSaved",true);

        Set<String> allIconKeys = cachedIcons.keySet();
        edit.putStringSet("icons",allIconKeys);
        for(String key: allIconKeys)
            edit.putString(key, cachedIcons.get(key).toString());

        Set<String> allBigKeys = cachedBigImages.keySet();
        edit.putStringSet("big",allBigKeys);
        for(String key: allBigKeys)
            edit.putString(key,cachedBigImages.get(key).toString());

        Set<String> allProfileImageKeys = cachedProfileImages.keySet();
        edit.putStringSet("profileImages",allProfileImageKeys);
        for(String key: allProfileImageKeys)
            edit.putString(key, cachedProfileImages.get(key).toString());

        edit.apply();

    }

    public void getCache(){
        Log.i(TAG,"getCache");
        SharedPreferences cache = context.getSharedPreferences("cache", Context.MODE_PRIVATE);

        if(cache.getBoolean("isSaved",false)) {
            Log.i(TAG,"isSaved");

            cachedIcons = new HashMap<>();
            Set<String> icons = new HashSet<>();
            icons = cache.getStringSet("icons", icons);
            for (String key : icons) {
                String s = "";
                s = cache.getString(key, s);
                cachedIcons.put(key, Uri.parse(s));
            }

            cachedBigImages = new HashMap<>();
            Set<String> bigImages = new HashSet<>();
            bigImages = cache.getStringSet("big",bigImages);
            for (String key : bigImages) {
                String s = "";
                s = cache.getString(key, s);
                cachedBigImages.put(key, Uri.parse(s));
            }

            cachedProfileImages = new HashMap<>();
            Set<String> profileImages = new HashSet<>();
            profileImages = cache.getStringSet("profileImages", profileImages);
            for (String key : profileImages) {
                String[] strings = cache.getString(key, "").split("#");
                cachedProfileImages.put(key, new UriNew(Uri.parse(strings[0]),Long.valueOf(strings[1])));
            }

        }
    }

    private class uploadBitmapWorker extends AsyncTask<Bitmap,Void,byte[]> {

        String path;
        BitmapUploadListener listener;

        uploadBitmapWorker(String path, BitmapUploadListener listener){
            this.path = path;
            this.listener = listener;
        }

        @Override
        protected byte[] doInBackground(Bitmap... params) {

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            params[0].compress(Bitmap.CompressFormat.WEBP,100,byteArrayOutputStream);

            return byteArrayOutputStream.toByteArray();
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);

            StorageReference reference = storage.getReference().child(path);
            reference.putBytes(bytes)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.i(TAG,"uploadBitmap: onFailure",exception);
                            Toast.makeText(context, exception.getMessage(), Toast.LENGTH_SHORT).show();
                            listener.onFailure(exception);
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            listener.onSuccess();
                        }
                    });
        }
    }

    private class uploadPicWorker extends AsyncTask<Uri,Void,byte[]> {

        String key;
        int i;
        KeepTrack listener;

        uploadPicWorker( String key, final int i, final KeepTrack listener){
            this.key = key;
            this.listener = listener;
            this.i = i;
        }

        @Override
        protected byte[] doInBackground(Uri... params) {
            Bitmap bmp = ImageUtils.compressImage(params[0].toString(), MAX_UPLOAD_RES[0],MAX_UPLOAD_RES[1], context );
            return BitmapHelper.bitmapToByteArray(bmp);
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);

            StorageReference reference = storage.getReference().child("images/" + key + "/" + i);
            reference.putBytes(bytes)
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
    }

    private class UriNew {
        public Uri uri;
        long timeStamp;

        UriNew(Uri uri, long t){
            this.uri = uri;
            timeStamp = t;
        }

        @Override
        public String toString() {
            return (uri.toString() + "#" + String.valueOf(timeStamp));
        }
    }

}
