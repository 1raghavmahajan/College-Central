package com.blackboxindia.TakeIT.Network;

import android.graphics.Bitmap;

public interface BitmapResultListener {

    void onSuccess(Bitmap bitmap);

    void onFailure(Exception e);

}
