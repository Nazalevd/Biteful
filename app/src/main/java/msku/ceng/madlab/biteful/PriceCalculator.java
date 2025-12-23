package msku.ceng.madlab.biteful;

import msku.ceng.madlab.biteful.database.CartItem;
import java.util.List;

public class PriceCalculator {

    public double calculateTotal(List<CartItem> items) {
        double total = 0;
        for (CartItem item : items) {
            total += item.price * item.quantity;
        }
        return total;
    }
}