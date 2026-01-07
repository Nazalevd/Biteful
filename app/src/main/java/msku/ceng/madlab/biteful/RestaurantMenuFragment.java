package msku.ceng.madlab.biteful;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

public class RestaurantMenuFragment extends Fragment {

    private List<FoodItem> fullMenuList = new ArrayList<>();
    private RecyclerView recyclerView;
    private String currentRestaurantName = "";

    private final int COLOR_ORANGE = 0xFFE69248;
    private final int COLOR_WHITE = 0xFFFFFFFF;
    private final int COLOR_GRAY_BG = 0xFFF0F0F0;
    private final int COLOR_BLACK_TEXT = 0xFF000000;

    public RestaurantMenuFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_restaurant_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvName = view.findViewById(R.id.tvMenuRestaurantName);
        TextView tvRating = view.findViewById(R.id.tvMenuRating);
        recyclerView = view.findViewById(R.id.rvMenu);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        View btnBack = view.findViewById(R.id.btnBackMenu);
        if (btnBack != null) btnBack.setOnClickListener(v -> androidx.navigation.Navigation.findNavController(v).popBackStack());

        if (getArguments() != null) {
            Restaurant restaurant = (Restaurant) getArguments().getSerializable("restaurantObj");
            if (restaurant != null) {
                currentRestaurantName = restaurant.getName();

                if (tvName != null) tvName.setText(restaurant.getName());
                if (tvRating != null) tvRating.setText(restaurant.getRating() + " • " + restaurant.getDeliveryTime());

                loadMenuForRestaurant(view, restaurant.getName());
            }
        } else {
            loadMenuForRestaurant(view, "Default");
        }
    }

    private void loadMenuForRestaurant(View view, String restaurantName) {
        Button btn1 = view.findViewById(R.id.btnCat1);
        Button btn2 = view.findViewById(R.id.btnCat2);
        Button btn3 = view.findViewById(R.id.btnCat3);
        Button btn4 = view.findViewById(R.id.btnCat4);

        fullMenuList.clear();

        if (restaurantName != null && (restaurantName.contains("Pizza") || restaurantName.contains("Pizz"))) {
            if(btn1 != null) btn1.setText("Pizzas");
            if(btn2 != null) btn2.setText("Slices");
            if(btn3 != null) btn3.setText("Drinks");
            if(btn4 != null) btn4.setText("Sauces");

            fullMenuList.add(new FoodItem("Pepperoni Pizza", "Spicy & Hot", 220.00, R.drawable.pepperoni, "Pizza"));
            fullMenuList.add(new FoodItem("Margarita Pizza", "Mozzarella Cheese", 180.00, R.drawable.margarita, "Pizza"));
            fullMenuList.add(new FoodItem("Mushroom Pizza", "Fresh Mushrooms", 195.00, R.drawable.mushroom_pizza, "Pizza"));
            fullMenuList.add(new FoodItem("Pizza Slice", "Single Slice", 40.00,R.drawable.pizza_slice, "Slice"));
            fullMenuList.add(new FoodItem("Cola", "Cold Drink", 50.00, R.drawable.cola, "Drink"));
            fullMenuList.add(new FoodItem("Ranch Sauce", "Dip Sauce", 15.00, R.drawable.ranch, "Sauce"));

            setupButtons(btn1, btn2, btn3, btn4, "Pizza", "Slice", "Drink", "Sauce");

        } else if (restaurantName != null && (restaurantName.contains("Sushi") || restaurantName.contains("sushi"))) {
            if(btn1 != null) btn1.setText("Sushi");
            if(btn2 != null) btn2.setText("Rolls");
            if(btn3 != null) btn3.setText("Noodles");
            if(btn4 != null) btn4.setText("Drinks");

            fullMenuList.add(new FoodItem("Salmon Nigiri", "Sushi Fresh Salmon", 120.00, R.drawable.salmon_nigiri, "Sushi"));
            fullMenuList.add(new FoodItem("California Roll", "Crab & Avocado", 180.00, R.drawable.california_roll, "Roll"));
            fullMenuList.add(new FoodItem("Veggie Noodles", "With Vegetables", 200.00,R.drawable.veggie_noodle, "Noodle"));
            fullMenuList.add(new FoodItem("Green Tea", "Hot Drink", 30.00,R.drawable.green_tea, "Drink"));
            fullMenuList.add(new FoodItem("Water", "Drink", 15.00, R.drawable.water, "Drink"));

            setupButtons(btn1, btn2, btn3, btn4, "Sushi", "Roll", "Noodle", "Drink");

        } else if (restaurantName != null && (restaurantName.contains("Doner") || restaurantName.contains("Döner"))) {
            if(btn1 != null) btn1.setText("Dürümler");
            if(btn2 != null) btn2.setText("Porsiyon");
            if(btn3 != null) btn3.setText("İçecekler");
            if(btn4 != null) btn4.setText("Tatlılar");

            fullMenuList.add(new FoodItem("Et Döner Dürüm", "Traditional Taste", 150.00, R.drawable.et_doner, "Dürüm"));
            fullMenuList.add(new FoodItem("Tavuk Döner Dürüm", "Classic Chicken", 100.00, R.drawable.tavuk_doner, "Dürüm"));
            fullMenuList.add(new FoodItem("Pilav Üstü Porsiyon", "With Rice", 200.00, R.drawable.pilav_ustu, "Porsiyon"));
            fullMenuList.add(new FoodItem("Ayran", "Fresh Yoghurt Drink", 20.00, R.drawable.ayran, "Drink"));
            fullMenuList.add(new FoodItem("Cola", "Cold Drink", 50.00, R.drawable.cola, "Drink"));
            fullMenuList.add(new FoodItem("Künefe", "Sweet Cheese", 120.00, R.drawable.kunefe, "Dessert"));

            setupButtons(btn1, btn2, btn3, btn4, "Dürüm", "Porsiyon", "Drink", "Dessert");

        } else if (restaurantName != null && (restaurantName.contains("Waffle") || restaurantName.contains("Dessert"))) {
            if(btn1 != null) btn1.setText("Waffles");
            if(btn2 != null) btn2.setText("Fruits");
            if(btn3 != null) btn3.setText("Drinks");
            if(btn4 != null) btn4.setText("Ice Cream");

            fullMenuList.add(new FoodItem("Classic Waffle", "Chocolate & Banana", 160.00, R.drawable.waffle, "Waffle"));
            fullMenuList.add(new FoodItem("Fruit Bomb", "Mixed Fruits", 180.00, R.drawable.fruits, "Fruit"));
            fullMenuList.add(new FoodItem("Chocolate Lover", "Dark & White Choco", 170.00, R.drawable.chocolate_waffle, "Waffle"));
            fullMenuList.add(new FoodItem("Coffee", "Hot Coffee", 60.00, R.drawable.coffee, "Drink"));
            fullMenuList.add(new FoodItem("Vanilla Ice Cream", "Cold", 40.00, R.drawable.vanilla_ice_cream, "Ice"));

            setupButtons(btn1, btn2, btn3, btn4, "Waffle", "Fruit", "Drink", "Ice");

        } else {
            if(btn1 != null) btn1.setText("Burgers");
            if(btn2 != null) btn2.setText("Menus");
            if(btn3 != null) btn3.setText("Drinks");
            if(btn4 != null) btn4.setText("Desserts");

            fullMenuList.add(new FoodItem("Burger Menu 1", "Cheeseburger + Fries", 350.99, R.drawable.burger_menu_1, "Burger"));
            fullMenuList.add(new FoodItem("Burger Menu 2", "Double Meat", 400.00, R.drawable.burger_menu_2, "Burger"));
            fullMenuList.add(new FoodItem("Chicken Burger", "Crispy Chicken", 250.00, R.drawable.chicken_burger, "Burger"));
            fullMenuList.add(new FoodItem("Cola", "Drink", 50.00, R.drawable.cola, "Drink"));
            fullMenuList.add(new FoodItem("Ice Cream", "Dessert", 40.00, R.drawable.ice_cream, "Dessert"));

            setupButtons(btn1, btn2, btn3, btn4, "Burger", "Menu", "Drink", "Dessert");
        }
    }

    private void setupButtons(Button b1, Button b2, Button b3, Button b4, String k1, String k2, String k3, String k4) {
        if(b1!=null) b1.setOnClickListener(v -> { filterList(k1); updateCategoryVisuals(b1, b2, b3, b4); });
        if(b2!=null) b2.setOnClickListener(v -> { filterList(k2); updateCategoryVisuals(b2, b1, b3, b4); });
        if(b3!=null) b3.setOnClickListener(v -> { filterList(k3); updateCategoryVisuals(b3, b1, b2, b4); });
        if(b4!=null) b4.setOnClickListener(v -> { filterList(k4); updateCategoryVisuals(b4, b1, b2, b3); });

        updateCategoryVisuals(b1, b2, b3, b4);
        filterList(k1);
    }

    private void updateCategoryVisuals(Button selectedBtn, Button... otherButtons) {
        if (selectedBtn != null) {
            selectedBtn.setBackgroundTintList(ColorStateList.valueOf(COLOR_ORANGE));
            selectedBtn.setTextColor(COLOR_WHITE);
        }
        for (Button btn : otherButtons) {
            if (btn != null) {
                btn.setBackgroundTintList(ColorStateList.valueOf(COLOR_GRAY_BG));
                btn.setTextColor(COLOR_BLACK_TEXT);
            }
        }
    }

    private void filterList(String keyword) {
        List<FoodItem> filteredList = new ArrayList<>();
        for (FoodItem item : fullMenuList) {
            if (item.getType().equalsIgnoreCase(keyword)) {
                filteredList.add(item);
            } else if (item.getName().toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(item);
            }
        }
        updateAdapter(filteredList);
    }

    private void updateAdapter(List<FoodItem> listToShow) {
        MenuAdapter adapter = new MenuAdapter(listToShow, item -> addToCart(item));
        recyclerView.setAdapter(adapter);
    }

    private void addToCart(FoodItem item) {
        CustomizeFoodBottomSheet bottomSheet = CustomizeFoodBottomSheet.newInstance(item, currentRestaurantName);
        bottomSheet.show(getParentFragmentManager(), "CustomizeSheet");
    }
}