package org.buildmlearn.toolkit.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.buildmlearn.toolkit.R;

public class FirstRunActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_run);

        YoYo.with(Techniques.BounceInUp)
                .duration(2700)
                .playOn(findViewById(R.id.first_name));
    }

}
