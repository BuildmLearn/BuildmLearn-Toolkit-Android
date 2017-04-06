package org.quizGen.shasha.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.quizGen.shasha.constant.Constants;

/**
 * @brief Activity responsible for changing settings from android settings menu
 * <p/>
 * This activity is started whenever users clicks App Settings under App notifications in android settings menu.
 */
public class SettingsLinkerActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtra(Constants.START_FRAGMENT, 3);
        startActivity(intent);
        finish();
    }
}
