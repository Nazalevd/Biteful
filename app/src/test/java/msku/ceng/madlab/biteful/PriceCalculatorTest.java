package msku.ceng.madlab.biteful;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;

public class PriceCalculatorTest {

    @Test
    public void total_price_calculation_is_correct() {

        List<CartItem> fakeCart = new ArrayList<>();

        fakeCart.add(new CartItem("Burger", 350.99, 1, "None", "Test Restaurant"));
        fakeCart.add(new CartItem("Cola", 50.00, 2, "None", "Test Restaurant"));

        double expectedPrice = 450.99;

        double actualPrice = PriceCalculator.calculateTotal(fakeCart);

        assertEquals(expectedPrice, actualPrice, 0.001);
    }

    @Test
    public void empty_cart_returns_zero() {
        List<CartItem> emptyCart = new ArrayList<>();

        double actualPrice = PriceCalculator.calculateTotal(emptyCart);

        assertEquals(0.0, actualPrice, 0.001);
    }
}