package org.buildmlearn.toolkit.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.ToolkitApplication;
import org.buildmlearn.toolkit.utilities.ZipUtils;

import java.io.IOException;

public class TestActivity extends AppCompatActivity {

    ToolkitApplication toolkit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        toolkit = (ToolkitApplication)getApplicationContext();


        Log.d(getClass().getName(),toolkit.getProjectDir());

        ZipUtils.copyAssets(this, "FlashCardTemplateApp_v2.0.apk", toolkit.getApkDir());

        try {
            ZipUtils.unZip(toolkit.getApkDir()+ "FlashCardTemplateApp_v2.0.apk", toolkit.getUnZipDir() + "FlashCardTemplateApp_v2.0");
        } catch (IOException e) {
            e.printStackTrace();
        }

        ZipUtils.zipFileAtPath(toolkit.getUnZipDir() + "FlashCardTemplateApp_v2.0", toolkit.getApkDir() + "/FlashCardTemplateApp_v2.1110.apk");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
