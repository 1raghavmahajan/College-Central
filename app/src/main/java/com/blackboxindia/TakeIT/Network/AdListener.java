package com.blackboxindia.TakeIT.Network;

import com.blackboxindia.TakeIT.dataModels.AdData;

public interface AdListener {

    void onSuccess(AdData adData);

    void onFailure(Exception e);

}
