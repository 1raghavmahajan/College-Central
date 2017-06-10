package com.blackboxindia.TakeIT.Network.Interfaces;

import com.blackboxindia.TakeIT.dataModels.UserInfo;
import com.google.firebase.auth.FirebaseAuth;


public interface onLoginListener {

    void onSuccess(FirebaseAuth Auth, UserInfo userInfo);

    void onFailure(Exception e);

}
