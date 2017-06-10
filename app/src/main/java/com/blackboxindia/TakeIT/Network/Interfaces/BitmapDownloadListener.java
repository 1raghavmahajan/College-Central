package com.blackboxindia.TakeIT.Network.Interfaces;

import android.graphics.Bitmap;

public interface BitmapDownloadListener {

    void onSuccess(Bitmap bitmap);

    void onFailure(Exception e);
}
