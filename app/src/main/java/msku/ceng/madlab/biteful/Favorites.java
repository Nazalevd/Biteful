package msku.ceng.madlab.biteful;

import java.io.Serializable;

public class Favorites implements Serializable {
    private String documentId;
    private String restaurantName;
    private String rating;
    private String deliveryTime;
    private int imageResId;

    public Favorites() { }

    public Favorites(String restaurantName, String rating, String deliveryTime, int imageResId) {
        this.restaurantName = restaurantName;
        this.rating = rating;
        this.deliveryTime = deliveryTime;
        this.imageResId = imageResId;
    }

    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }

    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }

    public String getDeliveryTime() { return deliveryTime; }
    public void setDeliveryTime(String deliveryTime) { this.deliveryTime = deliveryTime; }

    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
}