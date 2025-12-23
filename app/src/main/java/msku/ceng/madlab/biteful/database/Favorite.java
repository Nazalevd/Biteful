package msku.ceng.madlab.biteful.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorite_table")
public class Favorite {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String restaurantName;
    public String rating;
    public String deliveryTime;
    public int imageResId;

    public Favorite(String restaurantName, String rating, String deliveryTime, int imageResId) {
        this.restaurantName = restaurantName;
        this.rating = rating;
        this.deliveryTime = deliveryTime;
        this.imageResId = imageResId;
    }
}