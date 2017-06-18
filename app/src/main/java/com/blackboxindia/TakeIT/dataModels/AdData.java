package com.blackboxindia.TakeIT.dataModels;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class AdData implements Parcelable {

    private String adID;
    private String createdBy;
    private String title;
    private Integer price;
    private String description;
    private Integer numberOfImages;

    public AdData() {
        adID = "null";
    }

    public AdData(Bundle bundle) {
        adID = bundle.getString("adID","null");
        createdBy = bundle.getString("createdBy","null");
        title = bundle.getString("Title", "title");
        price = bundle.getInt("Price", -1);
        description = bundle.getString("Description","null");
        numberOfImages = bundle.getInt("numberOfImages",0);
    }

    //region Getters and Setters

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getAdID() {
        return adID;
    }

    public void setAdID(String adID) {
        this.adID = adID;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getNumberOfImages() {
        return numberOfImages;
    }

    public void setNumberOfImages(Integer numberOfImages) {
        this.numberOfImages = numberOfImages;
    }

    //endregion

    //region Parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.adID,
                this.createdBy,
                this.title,
                String.valueOf(this.price),
                this.description,
                String.valueOf(this.numberOfImages)
        });
    }

    private AdData(Parcel in) {
        adID = in.readString();
        createdBy = in.readString();
        title = in.readString();
        price = Integer.getInteger(in.readString());
        description = in.readString();
        numberOfImages = Integer.getInteger(in.readString());
    }

    public static final Creator<AdData> CREATOR = new Creator<AdData>() {
        @Override
        public AdData createFromParcel(Parcel in) {
            return new AdData(in);
        }

        @Override
        public AdData[] newArray(int size) {
            return new AdData[size];
        }
    };
    //endregion
}
