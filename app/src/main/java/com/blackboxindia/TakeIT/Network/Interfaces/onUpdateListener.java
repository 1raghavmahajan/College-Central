package com.blackboxindia.TakeIT.Network.Interfaces;

import com.blackboxindia.TakeIT.dataModels.UserInfo;


public interface onUpdateListener {

    void onSuccess(UserInfo userInfo);

    void onFailure(Exception e);

}
