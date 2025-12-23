package msku.ceng.madlab.biteful;

import java.io.Serializable;

public class Restaurant implements Serializable {
    private String name;
    private String rating; // Örn: "4.6 (3500+)"
    private String deliveryTime; // Örn: "15-20 min"
    private int imageResId; // Resim

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