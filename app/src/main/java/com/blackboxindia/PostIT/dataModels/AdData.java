package com.blackboxindia.PostIT.dataModels;

public class AdData {

    private String adID;
    private UserInfo createdBy;
    private String title;
    private Integer price;
    private String description;
    private Integer numberOfImages;
    private String category;
    private String type;
    private DateObject dateTime;

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

    public UserInfo getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UserInfo createdBy) {
        this.createdBy = createdBy;
    }

    public Integer getNumberOfImages() {
        return numberOfImages;
    }

    public void setNumberOfImages(Integer numberOfImages) {
        this.numberOfImages = numberOfImages;
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

    public DateObject getDateTime() {
        return dateTime;
    }

    public void setDateTime(DateObject dateTime) {
        this.dateTime = dateTime;
    }

    //endregion
}
