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

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvRestaurants;
    private RestaurantAdapter restaurantAdapter;
    private List<Restaurant> allRestaurants = new ArrayList<>();

    private RecyclerView rvCategories;
    private CategoryAdapter categoryAdapter;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        View searchBar = view.findViewById(R.id.homeSearchBar);
        if (searchBar != null) {
            searchBar.setOnClickListener(v ->
                    Navigation.findNavController(v).navigate(R.id.nav_search)
            );
        }

        rvRestaurants = view.findViewById(R.id.rvHomeRestaurants);
        rvRestaurants.setLayoutManager(new LinearLayoutManager(getContext()));

        loadRestaurantData();

        restaurantAdapter = new RestaurantAdapter(allRestaurants, restaurant -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("restaurantObj", restaurant);
            Navigation.findNavController(view).navigate(R.id.restaurantMenuFragment, bundle);
        });

        restaurantAdapter.setIsFavoritesList(false);
        rvRestaurants.setAdapter(restaurantAdapter);



        rvCategories = view.findViewById(R.id.rvCategories);

        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        List<Category> categories = new ArrayList<>();
        categories.add(new Category("All", R.drawable.cat_all));
        categories.add(new Category("Burger", R.drawable.cat_burger));
        categories.add(new Category("Pizza", R.drawable.cat_pizza));
        categories.add(new Category("Doner", R.drawable.cat_doner));
        categories.add(new Category("Sushi", R.drawable.cat_sushi));
        categories.add(new Category("Dessert", R.drawable.cat_dessert));

        categoryAdapter = new CategoryAdapter(categories, category -> {
            filterRestaurantsByCategory(category.getName());
        });

        rvCategories.setAdapter(categoryAdapter);
    }

    private void loadRestaurantData() {
        allRestaurants.clear();

        allRestaurants.add(new Restaurant("Campus Burger", "⭐ 4.6 (3500+)", "15-20 min", R.drawable.res_campus_burger));
        allRestaurants.add(new Restaurant("Kotekli Pizza", "⭐ 4.2 (1200+)", "30-45 min", R.drawable.res_kotekli_pizza));
        allRestaurants.add(new Restaurant("Doner House", "⭐ 4.8 (5000+)", "10-15 min", R.drawable.res_doner_house));
        allRestaurants.add(new Restaurant("Sushi Co", "⭐ 4.5 (800+)", "40-50 min", R.drawable.res_sushi_co));
        allRestaurants.add(new Restaurant("Waffle World", "⭐ 4.0 (900+)", "20-30 min", R.drawable.res_waffle_world));
        allRestaurants.add(new Restaurant("Burger King", "⭐ 3.9 (10k+)", "15-25 min", R.drawable.res_burger_king));
    }

    private void filterRestaurantsByCategory(String category) {
        if (category.equals("All")) {
            restaurantAdapter.setFilteredList(allRestaurants);
        } else {
            List<Restaurant> filtered = new ArrayList<>();
            for (Restaurant res : allRestaurants) {
                if (res.getName().toLowerCase().contains(category.toLowerCase())) {
                    filtered.add(res);
                }
                else if (category.equals("Dessert") && res.getName().contains("Waffle")) {
                    filtered.add(res);
                }
            }
            restaurantAdapter.setFilteredList(filtered);
        }
    }
}