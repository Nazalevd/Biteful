package msku.ceng.madlab.biteful;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import msku.ceng.madlab.biteful.database.BitefulDatabase;
import msku.ceng.madlab.biteful.database.CartItem;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class CustomizeFoodBottomSheet extends BottomSheetDialogFragment {

    private FoodItem foodItem;
    private String currentRestaurantName;
    private int quantity = 1;

    private TextView tvPrice, tvQuantity;
    private RadioGroup rgBurgerSize, rgPizzaSize, rgDrinkSize;


    public static CustomizeFoodBottomSheet newInstance(FoodItem item, String restaurantName) {
        CustomizeFoodBottomSheet fragment = new CustomizeFoodBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable("foodItem", item);
        args.putString("restaurantName", restaurantName);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_customize, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            foodItem = (FoodItem) getArguments().getSerializable("foodItem");
            currentRestaurantName = getArguments().getString("restaurantName");
        }

        TextView tvName = view.findViewById(R.id.tvProductName);
        tvPrice = view.findViewById(R.id.tvProductPrice);
        ImageView imgProduct = view.findViewById(R.id.imgProduct);
        tvQuantity = view.findViewById(R.id.tvQuantity);

        LinearLayout groupBurger = view.findViewById(R.id.groupBurger);
        LinearLayout groupPizza = view.findViewById(R.id.groupPizza);
        LinearLayout groupDrink = view.findViewById(R.id.groupDrink);

        rgBurgerSize = view.findViewById(R.id.rgBurgerSize);
        rgPizzaSize = view.findViewById(R.id.rgPizzaSize);
        rgDrinkSize = view.findViewById(R.id.rgDrinkSize);

        if (foodItem != null) {
            tvName.setText(foodItem.getName());
            imgProduct.setImageResource(foodItem.getImageResId());

            String type = foodItem.getType();
            if (type.equals("Burger")) groupBurger.setVisibility(View.VISIBLE);
            else if (type.equals("Pizza")) groupPizza.setVisibility(View.VISIBLE);
            else if (type.equals("Drink")) groupDrink.setVisibility(View.VISIBLE);

            updatePrice();
        }


        rgBurgerSize.setOnCheckedChangeListener((g, i) -> updatePrice());
        rgPizzaSize.setOnCheckedChangeListener((g, i) -> updatePrice());
        rgDrinkSize.setOnCheckedChangeListener((g, i) -> updatePrice());

        view.findViewById(R.id.btnPlus).setOnClickListener(v -> { quantity++; tvQuantity.setText(String.valueOf(quantity)); updatePrice(); });
        view.findViewById(R.id.btnMinus).setOnClickListener(v -> { if(quantity>1){ quantity--; tvQuantity.setText(String.valueOf(quantity)); updatePrice();} });

        view.findViewById(R.id.btnAddToCartFinal).setOnClickListener(v -> {
            StringBuilder details = new StringBuilder();

            if (foodItem.getType().equals("Burger")) {
                if(view.<RadioButton>findViewById(R.id.rbBurger180).isChecked()) details.append("180g (+50₺), ");
                else details.append("120g, ");
                if(view.<CheckBox>findViewById(R.id.cbNoOnion).isChecked()) details.append("No Onion, ");
                if(view.<CheckBox>findViewById(R.id.cbNoPickle).isChecked()) details.append("No Pickle, ");

            } else if (foodItem.getType().equals("Pizza")) {
                if(view.<RadioButton>findViewById(R.id.rbPizzaMedium).isChecked()) details.append("Medium (+40₺), ");
                else if(view.<RadioButton>findViewById(R.id.rbPizzaLarge).isChecked()) details.append("Large (+70₺), ");
                else details.append("Small, ");
                if(view.<RadioButton>findViewById(R.id.rbThickCrust).isChecked()) details.append("Thick Crust, ");

            } else if (foodItem.getType().equals("Drink")) {
                if(view.<RadioButton>findViewById(R.id.rbDrinkLarge).isChecked()) details.append("Large (+15₺), ");
                if(view.<CheckBox>findViewById(R.id.cbNoIce).isChecked()) details.append("No Ice, ");
            }

            String detailStr = details.toString();
            if(detailStr.length() > 2) detailStr = detailStr.substring(0, detailStr.length()-2);

            addToCartDatabase(foodItem.getName(), calculateSinglePrice(), quantity, detailStr);
        });
    }

    private double calculateSinglePrice() {
        double base = foodItem.getPrice();
        double extra = 0;
        if (foodItem.getType().equals("Burger")) {
            if (rgBurgerSize.getCheckedRadioButtonId() == R.id.rbBurger180) extra = 50;
        } else if (foodItem.getType().equals("Pizza")) {
            if (rgPizzaSize.getCheckedRadioButtonId() == R.id.rbPizzaMedium) extra = 40;
            else if (rgPizzaSize.getCheckedRadioButtonId() == R.id.rbPizzaLarge) extra = 70;
        } else if (foodItem.getType().equals("Drink")) {
            if (rgDrinkSize.getCheckedRadioButtonId() == R.id.rbDrinkLarge) extra = 15;
        }
        return base + extra;
    }

    private void updatePrice() {
        if(foodItem == null) return;
        double total = calculateSinglePrice() * quantity;
        tvPrice.setText(String.format("%.2f₺", total));
    }


    private void addToCartDatabase(String name, double price, int qty, String customizations) {
        BitefulDatabase.databaseWriteExecutor.execute(() -> {

            List<CartItem> currentCart = BitefulDatabase.getDatabase(requireContext()).bitefulDao().getAllCartItems();

            boolean canAdd = true;
            if (!currentCart.isEmpty()) {

                String existingRestaurant = currentCart.get(0).restaurantName;

                if (existingRestaurant != null && !existingRestaurant.equals(currentRestaurantName)) {

                    canAdd = false;
                }
            }

            if (canAdd) {

                CartItem item = new CartItem(name, price, qty, customizations, currentRestaurantName);
                BitefulDatabase.getDatabase(requireContext()).bitefulDao().insertCartItem(item);

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Added to Cart!", Toast.LENGTH_SHORT).show();
                        if (getActivity() instanceof MainActivity) ((MainActivity) getActivity()).updateCartBadge();
                        dismiss();
                    });
                }
            } else {

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "You can only order from one restaurant at a time! Please clear your cart first.", Toast.LENGTH_LONG).show();

                    });
                }
            }
        });
    }
}