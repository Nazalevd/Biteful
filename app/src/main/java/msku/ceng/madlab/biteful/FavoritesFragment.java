package msku.ceng.madlab.biteful;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import msku.ceng.madlab.biteful.database.BitefulDatabase;
import msku.ceng.madlab.biteful.database.Favorite;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerView;
    private View layoutEmptyFav;
    private RestaurantAdapter adapter;
    private List<Restaurant> favoriteList = new ArrayList<>();

    public FavoritesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rvFavorites);
        layoutEmptyFav = view.findViewById(R.id.layoutEmptyFav);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new RestaurantAdapter(favoriteList, restaurant -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("restaurantObj", restaurant);
            Navigation.findNavController(view).navigate(R.id.restaurantMenuFragment, bundle);
        });

        adapter.setIsFavoritesList(true);

        recyclerView.setAdapter(adapter);

        loadFavorites();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void loadFavorites() {
        BitefulDatabase.databaseWriteExecutor.execute(() -> {
            List<Favorite> dbFavorites = BitefulDatabase.getDatabase(requireContext()).bitefulDao().getAllFavorites();

            List<Restaurant> tempUiList = new ArrayList<>();
            for (Favorite f : dbFavorites) {
                tempUiList.add(new Restaurant(f.restaurantName, f.rating, f.deliveryTime, f.imageResId));
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    favoriteList.clear();
                    favoriteList.addAll(tempUiList);
                    adapter.notifyDataSetChanged();

                    if (favoriteList.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                        layoutEmptyFav.setVisibility(View.VISIBLE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                        layoutEmptyFav.setVisibility(View.GONE);
                    }
                });
            }
        });
    }
}