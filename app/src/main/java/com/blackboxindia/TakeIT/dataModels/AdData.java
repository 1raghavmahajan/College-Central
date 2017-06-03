package com.blackboxindia.TakeIT.dataModels;

import java.util.ArrayList;

/**
 * Created by Raghav on 01-Jun-17.
 */

public class AdData extends AdDataMini{

    private ArrayList<Integer> minorImages;
    String description;

    AdData(AdDataMini adDataMini) {
        super(adDataMini);
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
    public void setMinorImages(ArrayList<Integer> minorImages) {
        this.minorImages = minorImages;
    }

    /**
     * Todo:
     * get all images associated with the specific adID
     * Async
     */
    public ArrayList<Integer> getMinorImages() {
        return minorImages;
    }


}
