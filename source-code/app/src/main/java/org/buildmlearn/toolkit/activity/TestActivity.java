package org.buildmlearn.toolkit.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.constant.Constants;
import org.buildmlearn.toolkit.utilities.ZipUtils;

import java.io.IOException;

public class TestActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ZipUtils.copyAssets(this, "FlashCardTemplateApp_v2.0.apk", Constants.BUILD_M_LEARN_PATH);

        try {
            ZipUtils.unZip(Constants.BUILD_M_LEARN_PATH + "FlashCardTemplateApp_v2.0.apk", Constants.UNZIP + "FlashCardTemplateApp_v2.0");
        } catch (IOException e) {
            e.printStackTrace();
        }

        ZipUtils.zipFileAtPath(Constants.UNZIP + "FlashCardTemplateApp_v2.0", Constants.BUILD_M_LEARN_PATH + "/FlashCardTemplateApp_v2.1110.apk");
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
