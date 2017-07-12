package com.blackboxindia.PostIT.Network.Interfaces;

public interface BitmapUploadListener {

    void onSuccess();

    void onFailure(Exception e);
}
