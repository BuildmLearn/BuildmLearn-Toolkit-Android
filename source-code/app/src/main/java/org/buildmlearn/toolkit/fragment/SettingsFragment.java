package org.buildmlearn.toolkit.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import org.buildmlearn.toolkit.R;

/**
 * Created by abhishek on 21/06/15 at 9:51 PM.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fragment_settings);
    }
}
