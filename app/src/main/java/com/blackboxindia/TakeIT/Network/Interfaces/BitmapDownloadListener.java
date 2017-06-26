package com.blackboxindia.TakeIT.Network.Interfaces;

import android.net.Uri;

public interface BitmapDownloadListener {

    void onSuccess(Uri uri);

    void onFailure(Exception e);
}
