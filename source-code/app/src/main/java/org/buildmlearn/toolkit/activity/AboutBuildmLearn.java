package org.buildmlearn.toolkit.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import org.buildmlearn.toolkit.BuildConfig;
import org.buildmlearn.toolkit.R;

/**
 * @brief Gives brief info about BuildmLearn community and toolkit
 */

public class AboutBuildmLearn extends AppCompatActivity {
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_buildm_learn);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String versionName = BuildConfig.VERSION_NAME;
        ((TextView) findViewById(R.id.app_version)).setText("Version: " + versionName);
    }

}
