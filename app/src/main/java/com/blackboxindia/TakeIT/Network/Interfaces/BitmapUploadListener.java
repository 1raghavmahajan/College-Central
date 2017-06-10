package com.blackboxindia.TakeIT.Network.Interfaces;

public interface BitmapUploadListener {

    void onSuccess();

    void onFailure(Exception e);
}
