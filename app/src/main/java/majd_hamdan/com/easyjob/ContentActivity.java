package majd_hamdan.com.easyjob;

import android.app.FragmentManager;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import majd_hamdan.com.easyjob.ui.StickyFragmentNavigator;

public class ContentActivity extends AppCompatActivity {

    String TAG = "mh";
    public String NAV_Host_Frag_Tag = "navhosttag";
    Fragment navHostFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        if (savedInstanceState != null) {
            navHostFragment = getSupportFragmentManager().getFragment(
                    savedInstanceState, NAV_Host_Frag_Tag);
        } else {
            navHostFragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        }


        BottomNavigationView navView = findViewById(R.id.nav_view);


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_offers, R.id.navigation_history, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        navController.getNavigatorProvider().addNavigator(new StickyFragmentNavigator(this, navHostFragment.getChildFragmentManager(), R.id.nav_host_fragment));
        navController.setGraph(R.navigation.mobile_navigation);

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (menuItem.isChecked()) return false;

                switch (id)
                {
                    case R.id.navigation_offers :
                        navController.navigate(R.id.action_golbal_navigation_offers_self);
                        break;
                    case R.id.navigation_history :
                        navController.navigate(R.id.action_golbal_navigation_history_self);
                        break;
                    case R.id.navigation_profile :
                        navController.navigate(R.id.action_golbal_navigation_profile_self);
                        break;
                }
                return true;

            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        getSupportFragmentManager().putFragment(outState, NAV_Host_Frag_Tag, navHostFragment);
    }
}
