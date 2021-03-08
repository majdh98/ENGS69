package majd_hamdan.com.easyjob.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import majd_hamdan.com.easyjob.R;

public class ProfileFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // set the correct preferences
        setPreferencesFromResource(R.xml.profile_preferences, rootKey);

    }

}
