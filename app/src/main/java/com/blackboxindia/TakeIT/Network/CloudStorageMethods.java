package com.blackboxindia.TakeIT.Network;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("VisibleForTests")
public class CloudStorageMethods {

    private final static String TAG = CloudStorageMethods.class.getSimpleName() + " YOYO";

    private Context context;
    private FirebaseStorage storage;

    public CloudStorageMethods(Context context) {
        this.context = context;
        storage = FirebaseStorage.getInstance();
        cachedBigImages = new HashMap<>();
        cachedIcons = new HashMap<>();
    }

    private ArrayList<Integer> progress;
    private ArrayList<Boolean> allGood;
    private ArrayList<Integer> retryNo;
    void uploadPics(final ArrayList<Uri> imgURIs, final String key, final KeepTrackMain mainListener) {

        final ProgressDialog progressDialog = ProgressDialog.show(context, "Uploading Images", "", true, false);
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

        Bitmap bmp = ImageUtils.compressImage(uri.toString(), 800,800, context );

        StorageReference reference = storage.getReference().child("images/" + key + "/" + i);
        reference.putBytes(BitmapHelper.bitmapToByteArray(bmp))
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

    void uploadBitmap(String AdID, Bitmap bitmap, final BitmapUploadListener listener){

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP,100,byteArrayOutputStream);

        StorageReference reference = storage.getReference().child("images/"+AdID+"/0s");

        reference.putBytes(byteArrayOutputStream.toByteArray())
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
                Log.i(TAG,"uploadBitmap: onSuccess");
                listener.onSuccess();
            }
        });
    }

    private Map<String,Bitmap> cachedIcons;
    public void getMajorImage(String AdID, final BitmapDownloadListener listener) {

        Log.i(TAG,"getMajorImage for AdID: "+ AdID);

        if(cachedIcons.containsKey(AdID)) {
            listener.onSuccess(cachedIcons.get(AdID));
        }
        else {
            final long ONE_MEGABYTE = 1024 * 1024;
            storage.getReference().child("images/" + AdID + "/0s").getBytes(ONE_MEGABYTE)
                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            Log.i(TAG, "getMajorImage: onSuccess");
                            listener.onSuccess(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.i(TAG, "getMajorImage: onFailure");
                    listener.onFailure(exception);
                }
            });
        }
    }

    private Map<String,Uri> cachedBigImages;
    public void getBigImage(final String AdID, final int i, final ImageDownloadListener listener) {

        Log.i(TAG,"getBigImage for AdID: "+ AdID);
        if(cachedBigImages.containsKey(AdID + i)) {
            Log.i(TAG,"cachedBigImages "+ AdID + i );
            listener.onSuccess(cachedBigImages.get(AdID + i));
        }
        else {
            Log.i(TAG,"Getting image from internet "+ AdID + i );
            File localFile;
            try {

                localFile = File.createTempFile(AdID + i, ".bmp");
                final File finalLocalFile = localFile;
                storage.getReference().child("images/" + AdID + "/" + i).getFile(localFile)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                // Local temp file has been created
                                Log.i(TAG,"Image size: "+(taskSnapshot.getBytesTransferred()/(1024*1024)));
                                Log.i(TAG,"Uri: "+(Uri.fromFile(finalLocalFile)).toString());
                                cachedBigImages.put(AdID+i,Uri.fromFile(finalLocalFile));
                                listener.onSuccess(Uri.fromFile(finalLocalFile));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        listener.onFailure(exception);
                    }
                });

            } catch (IOException e) {
                listener.onFailure(e);
            }
        }
    }
}
