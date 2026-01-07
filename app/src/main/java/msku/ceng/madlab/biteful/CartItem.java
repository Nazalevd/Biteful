package msku.ceng.madlab.biteful;

import com.google.firebase.firestore.Exclude;
import java.io.Serializable;

public class CartItem implements Serializable {
    private String documentId;
    private String foodName;
    private double price;
    private int quantity;
    private String customizations;
    private String restaurantName;

    public CartItem() { }

    public CartItem(String foodName, double price, int quantity, String customizations, String restaurantName) {
        this.foodName = foodName;
        this.price = price;
        this.quantity = quantity;
        this.customizations = customizations;
        this.restaurantName = restaurantName;
    }

    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }

    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getCustomizations() { return customizations; }
    public void setCustomizations(String customizations) { this.customizations = customizations; }

    public String getRestaurantName() { return restaurantName; }
    public void setRestaurantName(String restaurantName) { this.restaurantName = restaurantName; }

    @Exclude
    public double getTotalPrice() {
        return price * quantity;
    }
}