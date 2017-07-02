package com.blackboxindia.TakeIT.dataModels;

public class AdData {

    private String adID;
    private String createdBy;
    private String title;
    private Integer price;
    private String description;
    private Integer numberOfImages;
    private String collegeName;
    private String category;
    private String type;

    public AdData() {
        adID = "null";
        category = "null";
        type = AdTypes.TYPE_SELL;
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

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        if(type!=null)
            return type;
        else
            return AdTypes.TYPE_SELL;
    }

    public void setType(String type) {
        this.type = type;
    }

    //endregion
}
