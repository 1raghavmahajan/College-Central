package com.blackboxindia.TakeIT.Network;

import com.blackboxindia.TakeIT.dataModels.UserInfo;
import com.google.firebase.auth.FirebaseAuth;


public interface onLoginResultListener {

    void onSuccess(FirebaseAuth Auth, UserInfo userInfo);

    void onFailure(Exception e);

}
