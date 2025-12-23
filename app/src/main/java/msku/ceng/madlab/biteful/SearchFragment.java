package msku.ceng.madlab.biteful;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView; // Geri tuşu için

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import msku.ceng.madlab.biteful.database.BitefulDatabase;
import msku.ceng.madlab.biteful.database.SearchHistory;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    private RecyclerView rvResults, rvHistory;
    private SearchView searchView;

    private RestaurantAdapter resultsAdapter;
    private SearchHistoryAdapter historyAdapter;

    private List<Restaurant> allRestaurants = new ArrayList<>();
    private List<SearchHistory> historyList = new ArrayList<>();

    public SearchFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        searchView = view.findViewById(R.id.searchView);
        rvResults = view.findViewById(R.id.rvSearchResults);
        rvHistory = view.findViewById(R.id.rvSearchHistory);

        View btnBack = view.findViewById(R.id.btnBackSearch);
        if(btnBack != null) btnBack.setOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        rvResults.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));

        loadDummyData();
        resultsAdapter = new RestaurantAdapter(allRestaurants, restaurant -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("restaurantObj", restaurant);
            Navigation.findNavController(view).navigate(R.id.restaurantMenuFragment, bundle);
        });
        resultsAdapter.setIsFavoritesList(false);
        rvResults.setAdapter(resultsAdapter);

        historyAdapter = new SearchHistoryAdapter(historyList, new SearchHistoryAdapter.OnHistoryClickListener() {
            @Override
            public void onItemClick(String query) {
                searchView.setQuery(query, true);
            }

            @Override
            public void onDeleteClick(SearchHistory history) {
                deleteHistory(history);
            }
        });
        rvHistory.setAdapter(historyAdapter);

        loadHistory();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                saveSearchToDb(query);
                filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    rvHistory.setVisibility(View.VISIBLE);
                    rvResults.setVisibility(View.GONE);
                    loadHistory();
                } else {
                    rvHistory.setVisibility(View.GONE);
                    rvResults.setVisibility(View.VISIBLE);
                    filter(newText);
                }
                return true;
            }
        });

        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && searchView.getQuery().toString().isEmpty()) {
                rvHistory.setVisibility(View.VISIBLE);
                rvResults.setVisibility(View.GONE);
            }
        });
    }


    private void filter(String text) {
        List<Restaurant> filteredList = new ArrayList<>();
        if (text != null && !text.isEmpty()) {
            String searchText = text.toLowerCase().trim();
            for (Restaurant item : allRestaurants) {
                if (item.getName().toLowerCase().contains(searchText)) {
                    filteredList.add(item);
                }
            }
        }
        resultsAdapter.setFilteredList(filteredList);
    }

    private void saveSearchToDb(String query) {
        if (query == null || query.trim().isEmpty()) return;

        BitefulDatabase.databaseWriteExecutor.execute(() -> {
            BitefulDatabase.getDatabase(requireContext()).bitefulDao().deleteSearchHistoryByQuery(query);
            BitefulDatabase.getDatabase(requireContext()).bitefulDao().insertSearchHistory(new SearchHistory(query, System.currentTimeMillis()));
        });
    }

    private void loadHistory() {
        BitefulDatabase.databaseWriteExecutor.execute(() -> {
            List<SearchHistory> list = BitefulDatabase.getDatabase(requireContext()).bitefulDao().getAllSearchHistory();

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    historyList.clear();
                    historyList.addAll(list);
                    historyAdapter.notifyDataSetChanged();

                    if (historyList.isEmpty()) {
                        rvHistory.setVisibility(View.GONE);
                    } else if (searchView.getQuery().toString().isEmpty()){
                        rvHistory.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    private void deleteHistory(SearchHistory history) {
        BitefulDatabase.databaseWriteExecutor.execute(() -> {
            BitefulDatabase.getDatabase(requireContext()).bitefulDao().deleteSearchHistory(history.id);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    historyList.remove(history);
                    historyAdapter.notifyDataSetChanged();
                });
            }
        });
    }

    private void loadDummyData() {
        allRestaurants.clear();

        allRestaurants.add(new Restaurant("Campus Burger", "⭐ 4.6 (3500+)", "15-20 min", R.drawable.res_campus_burger));
        allRestaurants.add(new Restaurant("Kotekli Pizza", "⭐ 4.2 (1200+)", "30-45 min", R.drawable.res_kotekli_pizza));
        allRestaurants.add(new Restaurant("Doner House", "⭐ 4.8 (5000+)", "10-15 min", R.drawable.res_doner_house));
        allRestaurants.add(new Restaurant("Sushi Co", "⭐ 4.5 (800+)", "40-50 min", R.drawable.res_sushi_co));
        allRestaurants.add(new Restaurant("Waffle World", "⭐ 4.0 (900+)", "20-30 min", R.drawable.res_waffle_world));
        allRestaurants.add(new Restaurant("Burger King", "⭐ 3.9 (10k+)", "15-25 min", R.drawable.res_burger_king));
    }
}