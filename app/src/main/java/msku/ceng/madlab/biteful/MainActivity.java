package msku.ceng.madlab.biteful;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.google.firebase.FirebaseApp.initializeApp(this);

        setContentView(R.layout.activity_main);


        bottomNav = findViewById(R.id.bottom_navigation);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(bottomNav, navController);

            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                int id = destination.getId();
                if (id == R.id.restaurantMenuFragment || id == R.id.nav_cart || id == R.id.ordersFragment || id == R.id.nav_search) {
                    bottomNav.setVisibility(View.GONE);
                } else {
                    bottomNav.setVisibility(View.VISIBLE);
                }
            });
        }
        updateCartBadge(0);
    }

    public void updateCartBadge(int count) {
        com.google.android.material.bottomnavigation.BottomNavigationView navView = findViewById(R.id.bottom_navigation);

        if (navView != null) {
            var badge = navView.getOrCreateBadge(R.id.nav_cart);

            if (count > 0) {
                badge.setVisible(true);
                badge.setNumber(count);
                badge.setBackgroundColor(android.graphics.Color.parseColor("#E69248"));
                badge.setBadgeTextColor(android.graphics.Color.WHITE);
            } else {
                badge.setVisible(false);
                badge.clearNumber();
            }
        }
    }
}