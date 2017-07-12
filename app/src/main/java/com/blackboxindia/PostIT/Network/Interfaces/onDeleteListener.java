package com.blackboxindia.PostIT.Network.Interfaces;

import com.blackboxindia.PostIT.dataModels.UserInfo;

/**
 * Created by Raghav on 14-Jun-17.
 */

public interface onDeleteListener {

    void onSuccess(UserInfo userInfo);
    void onFailure(Exception e);
}
