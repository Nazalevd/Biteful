package msku.ceng.madlab.biteful;

import java.io.Serializable;

public class Restaurant implements Serializable {
    private String name;
    private String rating;
    private String deliveryTime;
    private int imageResId;

    public Restaurant(String name, String rating, String deliveryTime, int imageResId) {
        this.name = name;
        this.rating = rating;
        this.deliveryTime = deliveryTime;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public String getRating() { return rating; }
    public String getDeliveryTime() { return deliveryTime; }
    public int getImageResId() { return imageResId; }
}