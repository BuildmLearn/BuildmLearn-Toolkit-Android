package org.buildmlearn.toolkit.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.ToolkitApplication;
import org.buildmlearn.toolkit.model.KeyStoreDetails;
import org.buildmlearn.toolkit.utilities.FileUtils;
import org.buildmlearn.toolkit.utilities.SignerThread;

import java.io.IOException;


public class TestActivity extends AppCompatActivity {

    ToolkitApplication toolkit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        toolkit = (ToolkitApplication) getApplicationContext();
        String password = getString(R.string.key_password);
        String aliasPassword = getString(R.string.alias_password);
        String alias = getString(R.string.alias_name);

        KeyStoreDetails details = new KeyStoreDetails("TestKeyStore.jks", password, alias, aliasPassword);
        String assetsApk = "quiz_template.apk";
        String finalApk = toolkit.getApkDir() + "final.apk";




        SignerThread thread = new SignerThread(getApplicationContext(), assetsApk, finalApk,details);
        thread.start();


    }

}
