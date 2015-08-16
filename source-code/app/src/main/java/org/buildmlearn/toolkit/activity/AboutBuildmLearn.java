package org.buildmlearn.toolkit.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

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

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            ((TextView)findViewById(R.id.app_version)).setText("Version: " + version);
        } catch (PackageManager.NameNotFoundException e) {
            ((TextView)findViewById(R.id.app_version)).setText("Version: 1.0");
            e.printStackTrace();
        }



    }

}
