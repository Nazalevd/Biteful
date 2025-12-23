package msku.ceng.madlab.biteful;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import msku.ceng.madlab.biteful.database.BitefulDatabase;
import msku.ceng.madlab.biteful.database.CartItem;
import msku.ceng.madlab.biteful.database.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView tvTotalPrice;
    private Button btnCompleteOrder;

    private View layoutEmptyCart;
    private View bottomSection;

    private CartAdapter adapter;
    private List<CartItem> cartItems = new ArrayList<>();

    public CartFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        recyclerView = view.findViewById(R.id.rvCartItems);
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        btnCompleteOrder = view.findViewById(R.id.btnCompleteOrder);
        layoutEmptyCart = view.findViewById(R.id.layoutEmptyCart);
        bottomSection = view.findViewById(R.id.bottomSection);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        adapter = new CartAdapter(cartItems, new CartAdapter.OnCartChangeListener() {
            @Override
            public void onCartChanged() {


                calculateTotal();

                updateEmptyState();


                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).updateCartBadge();
                }
            }
        });

        recyclerView.setAdapter(adapter);

        btnCompleteOrder.setOnClickListener(v -> completeOrder());

        View btnGoHome = view.findViewById(R.id.btnGoToMenu);
        if(btnGoHome != null) {
            btnGoHome.setOnClickListener(v ->
                    Navigation.findNavController(view).navigate(R.id.nav_home)
            );
        }

        View btnBack = view.findViewById(R.id.btnBackCart);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {

                Navigation.findNavController(v).popBackStack();
            });
        }

        loadCartItems();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCartItems();
    }

    private void loadCartItems() {
        BitefulDatabase.databaseWriteExecutor.execute(() -> {
            List<CartItem> items = BitefulDatabase.getDatabase(requireContext()).bitefulDao().getAllCartItems();

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    cartItems.clear();
                    cartItems.addAll(items);
                    adapter.notifyDataSetChanged();

                    calculateTotal();
                    updateEmptyState();


                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).updateCartBadge();
                    }
                });
            }
        });
    }

    private void updateEmptyState() {
        if (cartItems.isEmpty()) {

            recyclerView.setVisibility(View.GONE);
            bottomSection.setVisibility(View.GONE);
            layoutEmptyCart.setVisibility(View.VISIBLE);
        } else {

            recyclerView.setVisibility(View.VISIBLE);
            bottomSection.setVisibility(View.VISIBLE);
            layoutEmptyCart.setVisibility(View.GONE);
        }
    }

    private void calculateTotal() {
        double total = 0;
        for (CartItem item : cartItems) {
            total += item.price * item.quantity;
        }
        tvTotalPrice.setText(String.format("%.2f₺", total));
    }

    private void completeOrder() {
        if (cartItems.isEmpty()) return;


        StringBuilder summary = new StringBuilder();
        double totalAmount = 0;
        for (CartItem item : cartItems) {
            summary.append(item.quantity).append("x ").append(item.foodName).append(", ");
            totalAmount += item.price * item.quantity;
        }
        String itemsSummary = summary.length() > 2 ? summary.substring(0, summary.length() - 2) : summary.toString();


        String currentDate = new SimpleDateFormat("dd MMM HH:mm", Locale.getDefault()).format(new Date());


        final Order newOrder = new Order(currentDate, "Preparing...", totalAmount, itemsSummary);


        BitefulDatabase.databaseWriteExecutor.execute(() -> {

            BitefulDatabase.getDatabase(requireContext()).bitefulDao().insertOrder(newOrder);

            BitefulDatabase.getDatabase(requireContext()).bitefulDao().clearCart();

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {

                    cartItems.clear();
                    adapter.notifyDataSetChanged();
                    calculateTotal();
                    updateEmptyState();

                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).updateCartBadge();
                    }


                });
            }
        });
    }
}