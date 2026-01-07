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

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
            if (tvName != null) tvName.setText(foodItem.getName());
            if (imgProduct != null) imgProduct.setImageResource(foodItem.getImageResId());

            String type = foodItem.getType();
            if (type != null) {
                if (type.equals("Burger") && groupBurger != null) groupBurger.setVisibility(View.VISIBLE);
                else if (type.equals("Pizza") && groupPizza != null) groupPizza.setVisibility(View.VISIBLE);
                else if (type.equals("Drink") && groupDrink != null) groupDrink.setVisibility(View.VISIBLE);
            }
            updatePrice();
        }

        if (rgBurgerSize != null) rgBurgerSize.setOnCheckedChangeListener((g, i) -> updatePrice());
        if (rgPizzaSize != null) rgPizzaSize.setOnCheckedChangeListener((g, i) -> updatePrice());
        if (rgDrinkSize != null) rgDrinkSize.setOnCheckedChangeListener((g, i) -> updatePrice());

        View btnPlus = view.findViewById(R.id.btnPlus);
        View btnMinus = view.findViewById(R.id.btnMinus);

        if (btnPlus != null) btnPlus.setOnClickListener(v -> {
            quantity++;
            if (tvQuantity != null) tvQuantity.setText(String.valueOf(quantity));
            updatePrice();
        });

        if (btnMinus != null) btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                if (tvQuantity != null) tvQuantity.setText(String.valueOf(quantity));
                updatePrice();
            }
        });

        Button btnAdd = view.findViewById(R.id.btnAddToCartFinal);
        if (btnAdd != null) {
            btnAdd.setOnClickListener(v -> {
                v.setEnabled(false);

                ((Button)v).setText("Checking...");

                String customizations = buildCustomizationString(view);

                addToCartFirebase(foodItem.getName(), calculateSinglePrice(), quantity, customizations);
            });
        }
    }

    private double calculateSinglePrice() {
        if (foodItem == null) return 0;
        double base = foodItem.getPrice();
        double extra = 0;
        String type = foodItem.getType();

        if (type != null) {
            if (type.equals("Burger") && rgBurgerSize != null) {
                if (rgBurgerSize.getCheckedRadioButtonId() == R.id.rbBurger180) extra = 50;
            } else if (type.equals("Pizza") && rgPizzaSize != null) {
                if (rgPizzaSize.getCheckedRadioButtonId() == R.id.rbPizzaMedium) extra = 40;
                else if (rgPizzaSize.getCheckedRadioButtonId() == R.id.rbPizzaLarge) extra = 70;
            } else if (type.equals("Drink") && rgDrinkSize != null) {
                if (rgDrinkSize.getCheckedRadioButtonId() == R.id.rbDrinkLarge) extra = 15;
            }
        }
        return base + extra;
    }

    private void updatePrice() {
        if (foodItem == null) return;
        double total = calculateSinglePrice() * quantity;
        if (tvPrice != null) tvPrice.setText(String.format("%.2f₺", total));
    }

    private String buildCustomizationString(View view) {
        StringBuilder details = new StringBuilder();
        String type = foodItem.getType();

        if (type != null && type.equals("Burger")) {
            RadioButton rb180 = view.findViewById(R.id.rbBurger180);
            CheckBox cbOnion = view.findViewById(R.id.cbNoOnion);
            CheckBox cbPickle = view.findViewById(R.id.cbNoPickle);

            if (rb180 != null && rb180.isChecked()) details.append("180g (+50₺), ");
            else details.append("120g, ");
            if (cbOnion != null && cbOnion.isChecked()) details.append("No Onion, ");
            if (cbPickle != null && cbPickle.isChecked()) details.append("No Pickle, ");
        }
        String s = details.toString();
        if (s.length() > 2) s = s.substring(0, s.length() - 2);
        return s;
    }

    private void addToCartFirebase(String name, double price, int qty, String customizations) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("cart").limit(1).get().addOnSuccessListener(queryDocumentSnapshots -> {
            boolean differentRestaurant = false;

            if (!queryDocumentSnapshots.isEmpty()) {
                String cartRestaurant = queryDocumentSnapshots.getDocuments().get(0).getString("restaurantName");
                if (cartRestaurant != null && !cartRestaurant.equals(currentRestaurantName)) {
                    differentRestaurant = true;
                }
            }

            if (differentRestaurant) {
                Toast.makeText(getContext(), "Sepette başka restoranın ürünü var! Önce sepeti temizleyin.", Toast.LENGTH_LONG).show();
                resetButton();
            } else {
                processAddItem(db, name, price, qty, customizations);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Hata: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            resetButton();
        });
    }

    private void processAddItem(FirebaseFirestore db, String name, double price, int qty, String customizations) {
        db.collection("cart")
                .whereEqualTo("foodName", name)
                .whereEqualTo("restaurantName", currentRestaurantName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    String existingDocId = null;
                    int existingQty = 0;

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        CartItem item = doc.toObject(CartItem.class);
                        if (item.getCustomizations() != null && item.getCustomizations().equals(customizations)) {
                            existingDocId = doc.getId();
                            existingQty = item.getQuantity();
                            break;
                        }
                    }

                    if (existingDocId != null) {
                        db.collection("cart").document(existingDocId)
                                .update("quantity", existingQty + qty)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Sepet Güncellendi! 🛒", Toast.LENGTH_SHORT).show();
                                    dismiss();
                                });
                    } else {
                        CartItem newItem = new CartItem(name, price, qty, customizations, currentRestaurantName);
                        db.collection("cart").add(newItem)
                                .addOnSuccessListener(documentReference -> {
                                    Toast.makeText(getContext(), "Sepete Eklendi! 🛒", Toast.LENGTH_SHORT).show();
                                    dismiss();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Hata: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    resetButton();
                });
    }

    private void resetButton() {
        View btn = getView().findViewById(R.id.btnAddToCartFinal);
        if (btn != null) {
            btn.setEnabled(true);
            if (btn instanceof Button) ((Button) btn).setText("Add to Cart");
        }
    }
}