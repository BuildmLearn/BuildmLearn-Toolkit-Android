package org.buildmlearn.toolkit.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;

import android.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.fragment.DraftsFragment;
import org.buildmlearn.toolkit.fragment.HomeFragment;
import org.buildmlearn.toolkit.fragment.LoadApkFragment;
import org.buildmlearn.toolkit.fragment.LoadProjectFragment;
import org.buildmlearn.toolkit.fragment.SettingsFragment;
import org.buildmlearn.toolkit.utilities.SmoothNavigationToggle;

/**
 * @brief Home screen of the application containg all the menus and settings.
 */

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String FRAGMENT_TAG_HOME = "Home";
    private final String FRAGMENT_TAG_PROJECT = "Project";
    private final String FRAGMENT_TAG_APK = "Apk";
    private boolean backPressedOnce = false;

    private SmoothNavigationToggle smoothNavigationToggle;

    NavigationView navigationView;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("SkipTutorial", true);
        editor.apply();

        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View menuHeaderView = navigationView.getHeaderView(0);
        final TextView name = (TextView) menuHeaderView.findViewById(R.id.name);
        name.setText(String.format(" %s", prefs.getString(getString(R.string.key_user_name), "")));


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        smoothNavigationToggle = new SmoothNavigationToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                name.setText(String.format(" %s", prefs.getString(getString(R.string.key_user_name), "")));
                LoadProjectFragment f = (LoadProjectFragment) getFragmentManager().findFragmentByTag(FRAGMENT_TAG_PROJECT);
                if (f != null)
                    f.closeSearch();

                LoadApkFragment f2 = (LoadApkFragment) getFragmentManager().findFragmentByTag(FRAGMENT_TAG_APK);
                if (f2 != null)
                    f2.closeSearch();
            }
        };
        drawer.addDrawerListener(smoothNavigationToggle);
        smoothNavigationToggle.syncState();

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, new HomeFragment(), FRAGMENT_TAG_HOME).commit();
        navigationView.setCheckedItem(R.id.nav_home);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        final FragmentManager fragmentManager = getFragmentManager();
        int id = item.getItemId();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowCustomEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        switch(id){
            case R.id.nav_home:
                smoothNavigationToggle.runWhenIdle(new Runnable() {
                    @Override
                    public void run() {
                        fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .replace(R.id.container, new HomeFragment(), FRAGMENT_TAG_HOME).commit();
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle(R.string.app_name);
                        }
                    }
                });
                break;

            case R.id.nav_saved_projects:
                smoothNavigationToggle.runWhenIdle(new Runnable() {
                    @Override
                    public void run() {
                        fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .replace(R.id.container, new LoadProjectFragment(),FRAGMENT_TAG_PROJECT).commit();
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle(R.string.menu_load_project);
                        }
                    }
                });
                break;

            case R.id.nav_saved_apks:
                smoothNavigationToggle.runWhenIdle(new Runnable() {
                    @Override
                    public void run() {
                        fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .replace(R.id.container, new LoadApkFragment(),FRAGMENT_TAG_APK).commit();
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle(R.string.menu_load_apks);
                        }
                    }
                });
                break;

            case R.id.nav_drafts:
                smoothNavigationToggle.runWhenIdle(new Runnable() {
                    @Override
                    public void run() {
                        fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .replace(R.id.container, new DraftsFragment()).commit();
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle(R.string.menu_drafts);
                        }
                    }
                });
                break;

            case R.id.nav_settings:
                smoothNavigationToggle.runWhenIdle(new Runnable() {
                    @Override
                    public void run() {
                        fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .replace(R.id.container, new SettingsFragment()).commit();
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setTitle(R.string.menu_settings);
                        }
                    }
                });
                break;

            case R.id.nav_howto:
                final Intent intent = new Intent(this, TutorialActivity.class);
                smoothNavigationToggle.runWhenIdle(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                    }
                });
                break;
            case R.id.nav_about:
                final Intent intent2 = new Intent(this, AboutBuildmLearn.class);
                smoothNavigationToggle.runWhenIdle(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent2);
                    }
                });
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG_HOME);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fragment != null && fragment.isVisible()) {
            if(backPressedOnce){
                finish();
            }
            if(!backPressedOnce)
                Toast.makeText(this, "Tap back once more to exit.", Toast.LENGTH_SHORT).show();
            backPressedOnce=true;
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    backPressedOnce= false;
                }
            }, 2000);
        } else {
            fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .replace(R.id.container, new HomeFragment(), FRAGMENT_TAG_HOME).commit();
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(R.string.app_name);
            }
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }
}



