package com.blackboxindia.PostIT.Network.Interfaces;

import com.blackboxindia.PostIT.dataModels.AdData;

public interface newAdListener {

    void onSuccess(AdData adData);

    void onFailure(Exception e);

}
