package org.buildmlearn.toolkit.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.ToolkitApplication;

import java.io.File;

/**
 * Created by abhishek on 21/06/15 at 9:51 PM.
 */
public class SettingsFragment extends PreferenceFragment {

    private Preference prefUsername;
    private SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fragment_settings);
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        Preference button = findPreference(getString(R.string.key_delete_temporary_files));
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                String path = ToolkitApplication.getUnZipDir();
                float size = deleteDirectory(new File(path), 0);
                size = (float) ((float) Math.round((size / 1048576) * 100d) / 100d);
                if (size != 0) {
                    Toast.makeText(SettingsFragment.this.getActivity(), "Deleted " + size + " MB.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsFragment.this.getActivity(), "No Temp Files Found!", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        prefUsername = findPreference(getString(R.string.key_user_name));
        prefUsername.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                prefUsername.setSummary((String)newValue);
                return true;
            }
        });
        prefUsername.setSummary(preferences.getString(getString(R.string.key_user_name), ""));
    }

    public static float deleteDirectory(File file, float size) {
        if (file.exists()) {
            File[] listFiles = file.listFiles();
            if (listFiles == null) return 0;

            for (int i = 0; i < listFiles.length; i++) {
                if (listFiles[i].isDirectory()) {
                    size += deleteDirectory(listFiles[i], 0);
                } else {
                    size += listFiles[i].length();
                    listFiles[i].delete();
                }
            }
        }
        file.delete();
        return (size);
    }

}
