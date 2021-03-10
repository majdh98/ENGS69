package majd_hamdan.com.easyjob;

import android.app.FragmentManager;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import majd_hamdan.com.easyjob.ui.HistoryFragment;
import majd_hamdan.com.easyjob.ui.OffersFragment;
import majd_hamdan.com.easyjob.ui.ProfileFragment;

public class ContentActivity extends AppCompatActivity {

    String TAG = "mh";

    private Fragment offers_fragment;
    private Fragment history_fragment;
    private Fragment profile_fragment;
    private Fragment active_fragment;

    String OFFERS_TAG = "KS";
    String HISTORY_TAG = "HT";
    String PROFILE_TAG = "PT";
    String ACTIVE_FRAGMENT_TAGE = "AFT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        // hide the action bar at the top
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#002171")));


        if(savedInstanceState != null){
            offers_fragment  = getSupportFragmentManager().getFragment(savedInstanceState, OFFERS_TAG);
            history_fragment = getSupportFragmentManager().getFragment(savedInstanceState, HISTORY_TAG);
            profile_fragment = getSupportFragmentManager().getFragment(savedInstanceState, PROFILE_TAG);
            active_fragment = getSupportFragmentManager().getFragment(savedInstanceState, ACTIVE_FRAGMENT_TAGE);
            Log.d(TAG, "onCreate: " + offers_fragment);
        }else{
            offers_fragment = new OffersFragment();
            history_fragment = new HistoryFragment();
            profile_fragment = new ProfileFragment();
            active_fragment = offers_fragment;
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, offers_fragment).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, history_fragment).hide(history_fragment).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, profile_fragment).hide(profile_fragment).commit();
        }


        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if (menuItem.isChecked()) return false;

                switch (id)
                {
                    case R.id.navigation_offers :
                        getSupportFragmentManager().beginTransaction().hide(active_fragment).show(offers_fragment).commit();
                        active_fragment = offers_fragment;
                        break;
                    case R.id.navigation_history :
                        getSupportFragmentManager().beginTransaction().hide(active_fragment).show(history_fragment).commit();
                        active_fragment = history_fragment;
                        break;
                    case R.id.navigation_profile :
                        getSupportFragmentManager().beginTransaction().hide(active_fragment).show(profile_fragment).commit();
                        active_fragment = profile_fragment;
                        break;
                }
                return true;
            }
        });

    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        getSupportFragmentManager().putFragment(outState, OFFERS_TAG, offers_fragment);
        getSupportFragmentManager().putFragment(outState, HISTORY_TAG, history_fragment);
        getSupportFragmentManager().putFragment(outState, PROFILE_TAG, profile_fragment);
        getSupportFragmentManager().putFragment(outState, ACTIVE_FRAGMENT_TAGE, active_fragment);
    }

}
