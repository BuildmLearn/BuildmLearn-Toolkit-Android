package org.buildmlearn.toolkit.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.constant.Constants;

import io.fabric.sdk.android.Fabric;

/**
 * @brief Shown on application first launch.
 */
public class FirstRunActivity extends AppCompatActivity {

    private static final String FIRST_RUN = "first_run";

    private EditText name;
    private SharedPreferences prefs;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (prefs.getBoolean(FIRST_RUN, false)) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }
        setContentView(R.layout.activity_first_run);


        findViewById(R.id.focus_thief).clearFocus();
        YoYo.with(Techniques.BounceInUp)
                .duration(2700)
                .playOn(findViewById(R.id.first_name));


        name = (EditText) findViewById(R.id.first_name);


        name.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:

                            if (name.getText().toString().equals("")) {
                                Toast.makeText(getApplicationContext(), "Enter name", Toast.LENGTH_SHORT).show();
                                return false;
                            }


                            SharedPreferences.Editor editor = prefs.edit();

                            editor.putString(getString(R.string.key_user_name), name.getText().toString());
                            editor.putBoolean(FIRST_RUN, true);
                            editor.commit();
                            Intent intent = new Intent(getApplicationContext(), TutorialActivity.class);
                            intent.putExtra(Constants.START_ACTIVITY, true);
                            startActivity(intent);
                            finish();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }

}
