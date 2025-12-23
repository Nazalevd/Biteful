package msku.ceng.madlab.biteful;

import org.junit.Test;
import static org.junit.Assert.*;
import msku.ceng.madlab.biteful.database.CartItem;
import java.util.ArrayList;
import java.util.List;

public class PriceCalculatorTest {

    @Test
    public void total_price_calculation_is_correct() {
        PriceCalculator calculator = new PriceCalculator();
        List<CartItem> fakeCart = new ArrayList<>();

        fakeCart.add(new CartItem("Burger", 350.99, 1, "None"));
        fakeCart.add(new CartItem("Cola", 50.00, 2, "None"));

        double expectedPrice = 450.99;

        double actualPrice = calculator.calculateTotal(fakeCart);

        assertEquals(expectedPrice, actualPrice, 0.001);
    }

    @Test
    public void empty_cart_returns_zero() {
        PriceCalculator calculator = new PriceCalculator();
        List<CartItem> emptyCart = new ArrayList<>();

        double actualPrice = calculator.calculateTotal(emptyCart);

        assertEquals(0.0, actualPrice, 0.001);
    }
}