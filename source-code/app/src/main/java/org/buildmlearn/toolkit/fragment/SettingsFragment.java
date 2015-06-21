package org.buildmlearn.toolkit.fragment;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import org.buildmlearn.toolkit.R;

/**
 * Created by abhishek on 21/06/15 at 9:51 PM.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fragment_settings);

        Preference button = findPreference(getString(R.string.key_delete_temporary_files));
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(SettingsFragment.this.getActivity(), "Deleting temp files", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }
}
