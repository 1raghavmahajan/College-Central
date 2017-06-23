package com.blackboxindia.TakeIT.Network.Interfaces;

import com.blackboxindia.TakeIT.dataModels.UserInfo;


public interface onLoginListener {

    void onSuccess(UserInfo userInfo);

    void onFailure(Exception e);

}
