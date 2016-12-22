package org.buildmlearn.toolkit.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import com.crashlytics.android.Crashlytics;

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
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Fabric.with(this, new Crashlytics());
        if (prefs.getBoolean(FIRST_RUN, false)) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }
        setContentView(R.layout.activity_first_run);


        findViewById(R.id.focus_thief).clearFocus();
        Animation anim_bounceinup=AnimationUtils.loadAnimation(getBaseContext(),R.anim.bounceinup);
        name = (EditText) findViewById(R.id.first_name);
        name.startAnimation(anim_bounceinup);
        name.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:

                            if (name.getText().toString().equals("")) {
                                name.setError(getApplicationContext().getResources().getString(R.string.enter_name));
                                return false;
                            }
                            else if(!Character.isLetterOrDigit(name.getText().toString().charAt(0)))
                            {
                                name.setError(getApplicationContext().getResources().getString(R.string.valid_msg));
                                return false;
                            }


                            SharedPreferences.Editor editor = prefs.edit();

                            editor.putString(getString(R.string.key_user_name), name.getText().toString());
                            editor.putBoolean(FIRST_RUN, true);
                            editor.apply();
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
