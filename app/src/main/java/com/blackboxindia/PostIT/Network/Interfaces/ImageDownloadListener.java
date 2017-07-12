package com.blackboxindia.PostIT.Network.Interfaces;

import android.net.Uri;

public interface ImageDownloadListener {

    void onSuccess(Uri uri);
    void onFailure(Exception e);

}