package msku.ceng.madlab.biteful;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import msku.ceng.madlab.biteful.database.BitefulDatabase;
import msku.ceng.madlab.biteful.database.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartList;
    private OnCartChangeListener listener;

    public interface OnCartChangeListener {
        void onCartChanged();
    }

    public CartAdapter(List<CartItem> cartList, OnCartChangeListener listener) {
        this.cartList = cartList;
        this.listener = listener;
    }


    public CartAdapter(List<CartItem> cartList) {
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartList.get(position);

        holder.tvName.setText(item.foodName);
        holder.tvPrice.setText(item.price + "₺");
        holder.tvQuantity.setText("x" + item.quantity);
        holder.tvDetails.setText(item.customizations);

        holder.btnDelete.setOnClickListener(v -> {

            BitefulDatabase.databaseWriteExecutor.execute(() -> {
                BitefulDatabase.getDatabase(v.getContext()).bitefulDao().deleteCartItemById(item.id);


                ((Activity) v.getContext()).runOnUiThread(() -> {

                    cartList.remove(position);

                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, cartList.size());

                    if (listener != null) {
                        listener.onCartChanged();
                    }
                });
            });
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQuantity, tvDetails;
        ImageView btnDelete;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvCartItemName);
            tvPrice = itemView.findViewById(R.id.tvCartItemPrice);
            tvQuantity = itemView.findViewById(R.id.tvCartItemQuantity);
            tvDetails = itemView.findViewById(R.id.tvCartItemDetails);
            btnDelete = itemView.findViewById(R.id.btnDeleteCartItem);
    }
}}