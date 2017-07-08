package com.blackboxindia.TakeIT.Network.Interfaces;

import com.blackboxindia.TakeIT.dataModels.AdData;

public interface newAdListener {

    void onSuccess(AdData adData);

    void onFailure(Exception e);

}
