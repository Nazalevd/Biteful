package msku.ceng.madlab.biteful;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private List<CartItem> cartItemList = new ArrayList<>();

    private TextView tvTotal, tvSubtotal, tvDelivery;
    private Button btnCheckout;

    private FirebaseFirestore db;
    private ListenerRegistration firestoreListener;
    private double currentTotal = 0.0;

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

        db = FirebaseFirestore.getInstance();

        tvTotal = view.findViewById(R.id.tvTotalPrice);
        tvSubtotal = view.findViewById(R.id.tvSubtotalPrice);
        tvDelivery = view.findViewById(R.id.tvDeliveryPrice);
        btnCheckout = view.findViewById(R.id.btnCheckout);
        recyclerView = view.findViewById(R.id.rvCartItems);

        View btnBack = view.findViewById(R.id.btnBackCart);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                    getParentFragmentManager().popBackStack();
                }
            });
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new CartAdapter(cartItemList);
        recyclerView.setAdapter(adapter);

        listenToCartUpdates();

        btnCheckout.setOnClickListener(v -> {
            if (cartItemList.isEmpty()) {
                Toast.makeText(getContext(), "Your cart is empty!", Toast.LENGTH_SHORT).show();
            } else {
                placeOrderAndClearCart();
            }
        });
    }

    private void listenToCartUpdates() {
        firestoreListener = db.collection("cart").addSnapshotListener((value, error) -> {
            if (error != null) {
                return;
            }

            if (value != null) {
                cartItemList.clear();
                double subtotal = 0;
                int totalItemCount = 0;

                for (DocumentSnapshot doc : value.getDocuments()) {
                    CartItem item = doc.toObject(CartItem.class);
                    if (item != null) {
                        item.setDocumentId(doc.getId());
                        cartItemList.add(item);

                        subtotal += item.getPrice() * item.getQuantity();
                        totalItemCount += item.getQuantity();
                    }
                }

                adapter.notifyDataSetChanged();

                updatePriceUI(subtotal);

                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).updateCartBadge(totalItemCount);
                }
            }
        });
    }

    private void updatePriceUI(double subtotal) {
        double delivery = subtotal > 0 ? 25.0 : 0;
        currentTotal = subtotal + delivery;

        if (tvSubtotal != null) tvSubtotal.setText(String.format("%.2f₺", subtotal));
        if (tvDelivery != null) tvDelivery.setText(String.format("%.2f₺", delivery));
        if (tvTotal != null) tvTotal.setText(String.format("%.2f₺", currentTotal));
    }

    private void placeOrderAndClearCart() {
        btnCheckout.setEnabled(false);
        btnCheckout.setText("Processing Order...");

        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    performFirebaseOrder();
                });
            }
        }).start();
    }

    private void performFirebaseOrder() {
        StringBuilder summaryBuilder = new StringBuilder();

        for (CartItem item : cartItemList) {
            summaryBuilder.append(item.getFoodName())
                    .append(" x")
                    .append(item.getQuantity());

            if (item.getCustomizations() != null && !item.getCustomizations().isEmpty()) {
                summaryBuilder.append(" (").append(item.getCustomizations()).append(")");
            }

            summaryBuilder.append(", ");
        }

        String itemsSummary = summaryBuilder.toString();
        if (itemsSummary.length() > 2) {
            itemsSummary = itemsSummary.substring(0, itemsSummary.length() - 2);
        }

        Order newOrder = new Order(currentTotal, itemsSummary);

        db.collection("orders").add(newOrder)
                .addOnSuccessListener(documentReference -> {
                    String orderId = documentReference.getId();
                    db.collection("orders").document(orderId).update("documentId", orderId);

                    clearCart();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Order failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    btnCheckout.setEnabled(true);
                    btnCheckout.setText("Place Order");
                });
    }

    private void clearCart() {
        for (CartItem item : cartItemList) {
            if (item.getDocumentId() != null) {
                db.collection("cart").document(item.getDocumentId()).delete();
            }
        }

        Toast.makeText(getContext(), "Order placed successfully! 🍔", Toast.LENGTH_LONG).show();

        btnCheckout.setEnabled(true);
        btnCheckout.setText("Place Order");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (firestoreListener != null) {
            firestoreListener.remove();
        }
    }
}