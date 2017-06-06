package com.blackboxindia.TakeIT.dataModels;

import java.util.ArrayList;

public class AdData extends AdDataMini{

    private ArrayList<Integer> minorImages;
    private String description;

    AdData(AdDataMini adDataMini) {
        super(adDataMini);
    }

    /**
     * Todo:
     * get all images associated with the specific adID
     * Async
     */
    public ArrayList<Integer> getMinorImages() {
        return minorImages;
    }

    public void setMinorImages(ArrayList<Integer> minorImages) {
        this.minorImages = minorImages;
    }

    public void getAllData() {
        //Todo:
    }

    public String getDescription() {
        return description;
    }

    //region Getters and Setters
    public void setDescription(String description) {
        this.description = description;
    }
    //endregion
}
