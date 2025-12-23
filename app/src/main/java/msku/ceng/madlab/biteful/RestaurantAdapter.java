package msku.ceng.madlab.biteful;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
// Toast sildiğimiz için importu kaldırdık

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import msku.ceng.madlab.biteful.database.BitefulDatabase;
import msku.ceng.madlab.biteful.database.BitefulDao;
import msku.ceng.madlab.biteful.database.Favorite;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {

    private List<Restaurant> restaurantList;
    private OnItemClickListener listener;
    private boolean isFavoritesList = false;

    public interface OnItemClickListener {
        void onItemClick(Restaurant restaurant);
    }

    public RestaurantAdapter(List<Restaurant> restaurantList, OnItemClickListener listener) {
        this.restaurantList = restaurantList;
        this.listener = listener;
    }

    public void setIsFavoritesList(boolean isFavoritesList) {
        this.isFavoritesList = isFavoritesList;
    }

    public void setFilteredList(List<Restaurant> filteredList) {
        this.restaurantList = filteredList;
        notifyDataSetChanged();
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
        holder.tvTime.setText(restaurant.getDeliveryTime());
        holder.imgRes.setImageResource(restaurant.getImageResId());

        checkFavoriteStatus(holder, restaurant);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(restaurant));

        holder.btnFavorite.setOnClickListener(v -> {
            toggleFavorite(v, restaurant, holder.btnFavorite, holder.getBindingAdapterPosition());
        });
    }

    private void checkFavoriteStatus(RestaurantViewHolder holder, Restaurant restaurant) {
        BitefulDatabase.databaseWriteExecutor.execute(() -> {
            boolean isFav = BitefulDatabase.getDatabase(holder.itemView.getContext())
                    .bitefulDao().isFavorite(restaurant.getName());

            holder.itemView.post(() -> {
                if (isFav) {
                    holder.btnFavorite.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFADBB6));
                } else {
                    holder.btnFavorite.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFFFFFF));
                }
            });
        });
    }

    private void toggleFavorite(View view, Restaurant restaurant, ImageView btnHeart, int position) {
        BitefulDatabase.databaseWriteExecutor.execute(() -> {
            BitefulDao dao = BitefulDatabase.getDatabase(view.getContext()).bitefulDao();
            boolean isFav = dao.isFavorite(restaurant.getName());

            if (isFav) {
                dao.deleteFavoriteByName(restaurant.getName());

                ((Activity) view.getContext()).runOnUiThread(() -> {
                    if (isFavoritesList) {
                        if (position != RecyclerView.NO_POSITION && position < restaurantList.size()) {
                            restaurantList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, restaurantList.size());
                        }
                    } else {
                        btnHeart.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFFFFFF));
                    }
                });
            } else {
                dao.insertFavorite(new Favorite(restaurant.getName(), restaurant.getRating(), restaurant.getDeliveryTime(), restaurant.getImageResId()));

                ((Activity) view.getContext()).runOnUiThread(() -> {
                    btnHeart.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFADBB6));
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvRating, tvTime;
        ImageView imgRes, btnFavorite;

        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvRestaurantName);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvTime = itemView.findViewById(R.id.tvDeliveryTime);
            imgRes = itemView.findViewById(R.id.imgRestaurant);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }
}