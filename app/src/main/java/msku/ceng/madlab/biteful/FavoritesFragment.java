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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;

public class FavoritesFragment extends Fragment {

    private RecyclerView recyclerView;
    private FavoritesAdapter adapter;
    private List<Favorites> favoritesList = new ArrayList<>();
    private View layoutEmpty;

    public FavoritesFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rvFavorites);
        layoutEmpty = view.findViewById(R.id.layoutEmptyFavorites);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new FavoritesAdapter(favoritesList, item -> {
            Restaurant tempRestaurant = new Restaurant(
                    item.getRestaurantName(),
                    item.getRating(),
                    item.getDeliveryTime(),
                    item.getImageResId()
            );

            Bundle bundle = new Bundle();
            bundle.putSerializable("restaurantObj", tempRestaurant);
            Navigation.findNavController(view).navigate(R.id.restaurantMenuFragment, bundle);
        });

        recyclerView.setAdapter(adapter);

        loadFavorites();
    }

    private void loadFavorites() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("favorites").get().addOnSuccessListener(queryDocumentSnapshots -> {
            favoritesList.clear();
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                Favorites item = doc.toObject(Favorites.class);
                if (item != null) {
                    item.setDocumentId(doc.getId());
                    favoritesList.add(item);
                }
            }
            adapter.notifyDataSetChanged();

            if (layoutEmpty != null) {
                if (favoritesList.isEmpty()) {
                    layoutEmpty.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    layoutEmpty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}