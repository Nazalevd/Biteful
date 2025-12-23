package msku.ceng.madlab.biteful;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

public class AccountFragment extends Fragment {

    public AccountFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        View btnMyOrders = view.findViewById(R.id.btnMyOrders);

        if (btnMyOrders != null) {
            btnMyOrders.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Navigation.findNavController(v).navigate(R.id.action_account_to_orders);
                }
            });
        }


    }
}