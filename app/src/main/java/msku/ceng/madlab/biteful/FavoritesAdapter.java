package msku.ceng.madlab.biteful;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder> {

    private List<Favorites> favoritesList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Favorites favorites);
    }

    public FavoritesAdapter(List<Favorites> favoritesList, OnItemClickListener listener) {
        this.favoritesList = favoritesList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurant, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Favorites item = favoritesList.get(position);

        holder.tvName.setText(item.getRestaurantName());
        holder.tvRating.setText(item.getRating());
        holder.tvTime.setText(item.getDeliveryTime());
        holder.imgRestaurant.setImageResource(item.getImageResId());

        holder.btnFavorite.setImageResource(R.drawable.ic_heart);
        holder.btnFavorite.setColorFilter(android.graphics.Color.RED);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));

        holder.btnFavorite.setOnClickListener(v -> {
            if (item.getDocumentId() != null) {
                FirebaseFirestore.getInstance().collection("favorites")
                        .document(item.getDocumentId())
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            favoritesList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, favoritesList.size());
                            Toast.makeText(holder.itemView.getContext(), "Removed from Favorites", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoritesList.size();
    }

    static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvRating, tvTime;
        ImageView imgRestaurant, btnFavorite;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvRestaurantName);
            tvRating = itemView.findViewById(R.id.tvRestaurantRating);
            tvTime = itemView.findViewById(R.id.tvDeliveryTime);
            imgRestaurant = itemView.findViewById(R.id.imgRestaurant);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }
}