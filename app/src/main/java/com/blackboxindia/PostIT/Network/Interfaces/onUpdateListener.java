package com.blackboxindia.PostIT.Network.Interfaces;

import com.blackboxindia.PostIT.dataModels.UserInfo;


public interface onUpdateListener {

    void onSuccess(UserInfo userInfo);

    void onFailure(Exception e);

}
