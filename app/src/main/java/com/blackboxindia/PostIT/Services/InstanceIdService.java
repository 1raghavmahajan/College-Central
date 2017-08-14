package com.blackboxindia.PostIT.Services;

import android.content.Intent;

import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Raghav on 15-Aug-17.
 */

public class InstanceIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
    }

    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);
    }
}
