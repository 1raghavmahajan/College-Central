package com.blackboxindia.PostIT.Network.Interfaces;

import java.util.ArrayList;

public interface getCollegeDataListener {
    void onSuccess(ArrayList<String> data);
    void onFailure(Exception e);
}
