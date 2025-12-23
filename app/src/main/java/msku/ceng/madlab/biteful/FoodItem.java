package msku.ceng.madlab.biteful;

import java.io.Serializable;

public class FoodItem implements Serializable {
    private String name;
    private String description;
    private double price;
    private int imageResId;
    private String type;
    public FoodItem(String name, String description, double price, int imageResId, String type) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageResId = imageResId;
        this.type = type;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public int getImageResId() { return imageResId; }
    public String getType() { return type; }
}