package com.blackboxindia.PostIT.Network.Interfaces;

public interface KeepTrack {

    void onSuccess(int i);

    void failure(Exception e, int i);

    void onProgressUpdate(int i, int p);

}
