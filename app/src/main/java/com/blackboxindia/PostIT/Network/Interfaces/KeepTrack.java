package com.blackboxindia.PostIT.Network.Interfaces;

/**
 * Created by Raghav on 10-Jun-17.
 */
public interface KeepTrack {

    void onSuccess(int i);

    void failure(Exception e, int i);

    void onProgressUpdate(int i, int p);

}
