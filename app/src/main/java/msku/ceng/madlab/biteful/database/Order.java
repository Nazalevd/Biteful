package msku.ceng.madlab.biteful.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "order_table")
public class Order {
    @PrimaryKey(autoGenerate = true)
    public int orderId;

    public String date;
    public String status;
    public double totalAmount;
    public String itemsSummary;

    public Order(String date, String status, double totalAmount, String itemsSummary) {
        this.date = date;
        this.status = status;
        this.totalAmount = totalAmount;
        this.itemsSummary = itemsSummary;
    }
}