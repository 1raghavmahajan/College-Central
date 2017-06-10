package com.blackboxindia.TakeIT.Network.Interfaces;

import com.blackboxindia.TakeIT.dataModels.AdData;

import java.util.ArrayList;

public interface getAllAdsListener {

    void onSuccess(ArrayList<AdData> list);

    void onFailure(Exception e);
}
