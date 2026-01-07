package msku.ceng.madlab.biteful;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
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
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("search_history")
                .whereEqualTo("query", query)
                .get()
                .addOnSuccessListener(snapshots -> {
                    for (DocumentSnapshot doc : snapshots) {
                        doc.getReference().delete();
                    }
                    SearchHistory history = new SearchHistory(query, System.currentTimeMillis());
                    db.collection("search_history").add(history);
                });
    }

    private void loadHistory() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("search_history")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    historyList.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        SearchHistory item = doc.toObject(SearchHistory.class);
                        if (item != null) {
                            item.setDocumentId(doc.getId());
                            historyList.add(item);
                        }
                    }
                    historyAdapter.notifyDataSetChanged();
                    if (historyList.isEmpty()) {
                        rvHistory.setVisibility(View.GONE);
                    } else if (searchView.getQuery().toString().isEmpty()){
                        rvHistory.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void deleteHistory(SearchHistory history) {
        if (history.getDocumentId() != null) {
            FirebaseFirestore.getInstance().collection("search_history")
                    .document(history.getDocumentId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        historyList.remove(history);
                        historyAdapter.notifyDataSetChanged();
                    });
        }
    }

    private void loadDummyData() {
        allRestaurants.clear();
        allRestaurants.add(new Restaurant("Campus Burger", "⭐ 4.6 (3500+)", "15-20 min", R.drawable.res_campus_burger));
        allRestaurants.add(new Restaurant("Kotekli Pizza", "⭐ 4.2 (1200+)", "30-45 min", R.drawable.res_kotekli_pizza));
        allRestaurants.add(new Restaurant("Doner House", "⭐ 4.8 (5000+)", "10-15 min", R.drawable.res_doner_house));
        allRestaurants.add(new Restaurant("Sushi Co", "⭐ 4.5 (800+)", "40-50 min", R.drawable.res_sushi_co));
        allRestaurants.add(new Restaurant("Waffle World", "⭐ 4.0 (900+)", "20-30 min", R.drawable.res_waffle_world));
    }
}