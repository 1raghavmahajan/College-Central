package com.blackboxindia.PostIT.Network.Interfaces;

import com.blackboxindia.PostIT.dataModels.AdData;

import java.util.ArrayList;

public interface getAllAdsListener {

    void onSuccess(ArrayList<AdData> list);

    void onFailure(Exception e);
}
