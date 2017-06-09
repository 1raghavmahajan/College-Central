package com.blackboxindia.TakeIT.dataModels;

import android.os.Bundle;

import java.util.ArrayList;

public class AdData extends AdDataMini{

    private ArrayList<String> minorImages;
    private String description;

    AdData(AdDataMini adDataMini) {
        super(adDataMini);
    }

    public AdData(Bundle bundle) {
        super(bundle);
    }

    /**
     * Todo:
     * get all images associated with the specific adID
     * Async
     */
    public ArrayList<String> getMinorImages() {
        return minorImages;
    }

    //region Getters and Setters
    public void setMinorImages(ArrayList<String> minorImages) {
        this.minorImages = minorImages;
    }

    public void getAllData() {
        //Todo:
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    //endregion
}
