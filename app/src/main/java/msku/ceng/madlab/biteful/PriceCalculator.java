package msku.ceng.madlab.biteful;

import java.util.List;

public class PriceCalculator {

    public static double calculateTotal(List<CartItem> items) {
        double total = 0;
        for (CartItem item : items) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }
}