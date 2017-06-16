package com.blackboxindia.TakeIT.Network.Interfaces;

import com.blackboxindia.TakeIT.dataModels.UserInfo;

/**
 * Created by Raghav on 14-Jun-17.
 */

public interface onDeleteListener {

    void onSuccess(UserInfo userInfo);
    void onFailure(Exception e);
}
