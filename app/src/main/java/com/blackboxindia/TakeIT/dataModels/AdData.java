package com.blackboxindia.TakeIT.dataModels;

import android.graphics.Bitmap;
import android.os.Bundle;

import java.util.ArrayList;

public class AdData extends AdDataMini{

    private ArrayList<Bitmap> minorImages;
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
    public ArrayList<Bitmap> getMinorImages() {
        return minorImages;
    }

    public void setMinorImages(ArrayList<Bitmap> minorImages) {
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
