package msku.ceng.madlab.biteful;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Order implements Serializable {
    private String documentId;
    private String date;
    private String status;
    private double totalAmount;
    private String itemsSummary;

    public Order() { }

    public Order(double totalAmount, String itemsSummary) {
        this.totalAmount = totalAmount;
        this.itemsSummary = itemsSummary;
        this.status = "Preparing";

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        this.date = sdf.format(new Date());
    }

    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getItemsSummary() { return itemsSummary; }
    public void setItemsSummary(String itemsSummary) { this.itemsSummary = itemsSummary; }
}