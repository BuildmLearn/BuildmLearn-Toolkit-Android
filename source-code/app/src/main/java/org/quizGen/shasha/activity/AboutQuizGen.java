package org.quizGen.shasha.activity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;


import org.quizGen.shasha.R;

import java.util.Locale;

public class AboutQuizGen extends AppCompatActivity {
    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_quiz_gen);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        PackageInfo pInfo;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            assert findViewById(R.id.app_version) != null;
            ((TextView) findViewById(R.id.app_version)).setText(String.format(Locale.ENGLISH, "Version: %s", version));
        } catch (PackageManager.NameNotFoundException e) {
            assert findViewById(R.id.app_version) != null;
            assert ((TextView) findViewById(R.id.app_version)) != null;
            ((TextView) findViewById(R.id.app_version)).setText("Version: 2.5.0");
            e.printStackTrace();
        }


    }

}
