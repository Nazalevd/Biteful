package msku.ceng.madlab.biteful;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import msku.ceng.madlab.biteful.database.BitefulDatabase;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_navigation);


        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(bottomNav, navController);


            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                int id = destination.getId();


                if (id == R.id.restaurantMenuFragment || id == R.id.nav_cart || id == R.id.ordersFragment) {
                    bottomNav.setVisibility(View.GONE);
                }

                else {
                    bottomNav.setVisibility(View.VISIBLE);
                }
            });
        }


        updateCartBadge();
    }


    public void updateCartBadge() {
        BitefulDatabase.databaseWriteExecutor.execute(() -> {

            int count = BitefulDatabase.getDatabase(this).bitefulDao().getAllCartItems().size();

            runOnUiThread(() -> {
                if (count > 0) {
                    var badge = bottomNav.getOrCreateBadge(R.id.nav_cart);
                    badge.setVisible(true);
                    badge.setNumber(count);
                    badge.setBackgroundColor(0xFFE69248);
                } else {
                    var badge = bottomNav.getBadge(R.id.nav_cart);
                    if (badge != null) {
                        badge.setVisible(false);
                    }
                }
            });
        });
    }
}