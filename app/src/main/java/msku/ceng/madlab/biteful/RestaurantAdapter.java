package msku.ceng.madlab.biteful;

import android.content.Context;
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

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {

    private List<Restaurant> restaurantList;
    private OnRestaurantClickListener listener;
    private boolean isFavoritesList = false;

    public interface OnRestaurantClickListener {
        void onRestaurantClick(Restaurant restaurant);
    }

    public RestaurantAdapter(List<Restaurant> restaurantList, OnRestaurantClickListener listener) {
        this.restaurantList = restaurantList;
        this.listener = listener;
    }

    public void setFilteredList(List<Restaurant> filteredList) {
        this.restaurantList = filteredList;
        notifyDataSetChanged();
    }

    public void setIsFavoritesList(boolean isFavoritesList) {
        this.isFavoritesList = isFavoritesList;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_restaurant, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);

        holder.tvName.setText(restaurant.getName());
        holder.tvRating.setText(restaurant.getRating());
        holder.tvDeliveryTime.setText(restaurant.getDeliveryTime());
        holder.imgRestaurant.setImageResource(restaurant.getImageResId());

        holder.itemView.setOnClickListener(v -> listener.onRestaurantClick(restaurant));

        checkIfFavorite(holder, restaurant.getName());

        holder.btnFavorite.setOnClickListener(v -> toggleFavorite(holder, restaurant));
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    private void checkIfFavorite(RestaurantViewHolder holder, String restaurantName) {
        FirebaseFirestore.getInstance().collection("favorites")
                .whereEqualTo("restaurantName", restaurantName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        holder.btnFavorite.setImageResource(R.drawable.ic_heart);
                        holder.btnFavorite.setColorFilter(android.graphics.Color.RED);
                        holder.btnFavorite.setTag("fav");
                    } else {
                        holder.btnFavorite.setImageResource(R.drawable.ic_heart);
                        holder.btnFavorite.clearColorFilter();
                        holder.btnFavorite.setTag("not_fav");
                    }
                });
    }

    private void toggleFavorite(RestaurantViewHolder holder, Restaurant restaurant) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String currentTag = (String) holder.btnFavorite.getTag();

        if ("fav".equals(currentTag)) {
            db.collection("favorites")
                    .whereEqualTo("restaurantName", restaurant.getName())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (com.google.firebase.firestore.DocumentSnapshot doc : queryDocumentSnapshots) {
                            db.collection("favorites").document(doc.getId()).delete();
                        }
                        holder.btnFavorite.clearColorFilter();
                        holder.btnFavorite.setTag("not_fav");
                        Toast.makeText(holder.itemView.getContext(), "Removed from Favorites", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Favorites newFav = new Favorites(
                    restaurant.getName(),
                    restaurant.getRating(),
                    restaurant.getDeliveryTime(),
                    restaurant.getImageResId()
            );

            db.collection("favorites").add(newFav)
                    .addOnSuccessListener(documentReference -> {
                        holder.btnFavorite.setColorFilter(android.graphics.Color.RED);
                        holder.btnFavorite.setTag("fav");
                        Toast.makeText(holder.itemView.getContext(), "Added to Favorites", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    public static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvRating, tvDeliveryTime;
        ImageView imgRestaurant, btnFavorite;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvRestaurantName);
            tvRating = itemView.findViewById(R.id.tvRestaurantRating);
            tvDeliveryTime = itemView.findViewById(R.id.tvDeliveryTime);
            imgRestaurant = itemView.findViewById(R.id.imgRestaurant);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }
}