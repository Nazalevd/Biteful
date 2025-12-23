package msku.ceng.madlab.biteful;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import msku.ceng.madlab.biteful.database.BitefulDatabase;
import msku.ceng.madlab.biteful.database.Order;
import java.util.ArrayList;
import java.util.List;

public class OrdersFragment extends Fragment {

    private RecyclerView recyclerView;
    private OrdersAdapter adapter;
    private List<Order> orderList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.rvOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new OrdersAdapter(orderList);
        recyclerView.setAdapter(adapter);

        View btnBack = view.findViewById(R.id.btnBackOrders);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                androidx.navigation.Navigation.findNavController(v).popBackStack();
            });
        }

        loadOrders();
    }

    private void loadOrders() {
        BitefulDatabase.databaseWriteExecutor.execute(() -> {
            List<Order> orders = BitefulDatabase.getDatabase(requireContext()).bitefulDao().getAllOrders();
            getActivity().runOnUiThread(() -> {
                orderList.clear();
                orderList.addAll(orders);
                adapter.notifyDataSetChanged();
            });
        });
    }
}