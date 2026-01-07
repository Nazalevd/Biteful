package msku.ceng.madlab.biteful;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItemList;
    private FirebaseFirestore db;

    public CartAdapter(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItemList.get(position);

        holder.tvName.setText(item.getFoodName());
        if (item.getCustomizations() != null && !item.getCustomizations().isEmpty()) {
            holder.tvName.append("\n(" + item.getCustomizations() + ")");
            holder.tvName.setTextSize(14);
        }

        holder.tvPrice.setText(String.format("%.2f₺", item.getPrice() * item.getQuantity()));
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        holder.btnDecrease.setOnClickListener(v -> {
            if (item.getDocumentId() == null) return;

            if (item.getQuantity() > 1) {
                db.collection("cart").document(item.getDocumentId())
                        .update("quantity", item.getQuantity() - 1);
            } else {
                db.collection("cart").document(item.getDocumentId()).delete();
            }
        });

        holder.btnIncrease.setOnClickListener(v -> {
            if (item.getDocumentId() == null) return;

            db.collection("cart").document(item.getDocumentId())
                    .update("quantity", item.getQuantity() + 1);
        });
    }

    @Override
    public int getItemCount() { return cartItemList.size(); }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQuantity;
        ImageButton btnDecrease, btnIncrease;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCartFoodName);
            tvPrice = itemView.findViewById(R.id.tvCartPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
        }
    }
}