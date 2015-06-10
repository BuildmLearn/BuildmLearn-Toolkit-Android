package org.buildmlearn.toolkit.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.ToolkitApplication;
import org.buildmlearn.toolkit.utilities.FileUtils;
import org.buildmlearn.toolkit.utilities.SignerThread;


public class TestActivity extends AppCompatActivity {

    ToolkitApplication toolkit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        toolkit = (ToolkitApplication) getApplicationContext();


        Log.d(getClass().getName(), toolkit.getProjectDir());

//        FileUtils.copyAssets(this, "app_debug.apk", toolkit.getApkDir());
//        FileUtils.copyAssets(this, "key", toolkit.getApkDir());
//
//        String inputFile = toolkit.getApkDir() + "app_debug.apk";
//        String outputFile = toolkit.getApkDir() + "post.apk";
//        String keyPath = toolkit.getApkDir() + "key";
//        String password = "xxxxxxxxx";
//        String alias = "xxxxxxxxx";
//
//
//        SignerThread thread = new SignerThread(getApplicationContext(), inputFile, outputFile, keyPath, password, alias, password);
//        thread.start();

//        try {
//            FileUtils.unZip(toolkit.getApkDir() + "FlashCardTemplateApp_v2.0.apk", toolkit.getUnZipDir() + "FlashCardTemplateApp_v2.0");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        FileUtils.zipFileAtPath(toolkit.getUnZipDir() + "FlashCardTemplateApp_v2.0", toolkit.getApkDir() + "/FlashCardTemplateApp_v2.1110.apk");
    }

}
