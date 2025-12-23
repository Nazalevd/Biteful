package msku.ceng.madlab.biteful.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cart_table")
public class CartItem {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String foodName;
    public double price;
    public int quantity;
    public String customizations;
    public String restaurantName;


    public CartItem(String foodName, double price, int quantity, String customizations, String restaurantName) {
        this.foodName = foodName;
        this.price = price;
        this.quantity = quantity;
        this.customizations = customizations;
        this.restaurantName = restaurantName;
    }
}