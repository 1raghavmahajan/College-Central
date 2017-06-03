package com.blackboxindia.TakeIT.dataModels;

import com.blackboxindia.TakeIT.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Raghav on 01-Jun-17.
 */

public class AdDataMini {

    private Integer adID;

    private String title;
    private Integer majorImage;
    private Integer price;

    AdDataMini(){
        adID = null;
    }
    AdDataMini(AdDataMini adDataMini){
        adID = adDataMini.getAdID();
        title = adDataMini.getTitle();
        majorImage = adDataMini.getMajorImage();
        price = adDataMini.getPrice();
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

    public void setMajorImage(Integer majorImage) {
        this.majorImage = majorImage;
    }

    /**
     * Todo:
     * getMajorImage to get the Image of the ad from network
     */
    public Integer getMajorImage() {
        return majorImage;
    }

    public Integer getAdID() {
        return adID;
    }

    public void setAdID(Integer adID) {
        this.adID = adID;
    }

    public static List<AdDataMini> getList() {

        List<AdDataMini> data = new ArrayList<>();

        int[] images = {
                R.drawable.ani_cat_one,
                R.drawable.ani_cat_two,
                R.drawable.ani_cat_three,
                R.drawable.ani_cat_four,
                R.drawable.ani_cat_five,
                R.drawable.ani_cat_six,
                R.drawable.ani_cat_seven,

                R.drawable.ani_dog_one,
                R.drawable.ani_dog_two,
                R.drawable.ani_dog_three,
                R.drawable.ani_dog_four,
                R.drawable.ani_dog_five,

                R.drawable.ani_deer_one,
                R.drawable.ani_deer_two,
                R.drawable.ani_deer_three,
                R.drawable.ani_deer_four,

                R.drawable.bird_parrot_one,
                R.drawable.bird_parrot_two,
                R.drawable.bird_parrot_three,
                R.drawable.bird_parrot_four,
                R.drawable.bird_parrot_five,
                R.drawable.bird_parrot_six,
                R.drawable.bird_parrot_seven,
                R.drawable.bird_parrot_eight
        };

        String[] Titles = {"Cat 1", "Cat 2", "Cat 3", "Cat 4" ,"Cat 5" ,"Cat 6","Cat 7",
                "Dog 1","Dog 2","Dog 3","Dog 4","Dog 5",
                "Deer 1","Deer 2","Deer 3","Deer 4",
                " Parrot 1"," Parrot 2"," Parrot 3"," Parrot 4"," Parrot 5"," Parrot 6"," Parrot 7"," Parrot 8"};

        for (int i = 0; i < images.length; i++) {

            AdDataMini current = new AdDataMini();
            current.setTitle(Titles[i]);
            current.setMajorImage(images[i]);
            current.setPrice(i);
            data.add(current);
        }

        return data;

    }
}
