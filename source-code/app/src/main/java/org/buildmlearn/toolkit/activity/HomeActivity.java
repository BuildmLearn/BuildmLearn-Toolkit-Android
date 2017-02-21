package org.buildmlearn.toolkit.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.drawable.ColorDrawable;
import android.os.Build;


import android.net.Uri;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;

import android.app.FragmentManager;

import android.support.v4.content.ContextCompat;



import android.support.v4.view.GravityCompat;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;


import android.util.Log;
import android.view.Gravity;
import android.view.Menu;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.squareup.picasso.Picasso;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.fragment.DraftsFragment;
import org.buildmlearn.toolkit.fragment.HomeFragment;
import org.buildmlearn.toolkit.fragment.LoadApkFragment;
import org.buildmlearn.toolkit.fragment.LoadProjectFragment;
import org.buildmlearn.toolkit.fragment.SettingsFragment;
import org.buildmlearn.toolkit.utilities.CircleTransform;
import org.buildmlearn.toolkit.utilities.SmoothNavigationToggle;

import static org.buildmlearn.toolkit.R.drawable.logo_70;

/**
 * @brief Home screen of the application containg all the menus and settings.
 */

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,
        GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks{

    private final String FRAGMENT_TAG_HOME = "Home";
    private final String FRAGMENT_TAG_PROJECT = "Project";
    private final String FRAGMENT_TAG_APK = "Apk";
    private boolean backPressedOnce = false;

    private static final int REQUEST_DRIVE_SIGNIN = 123;
    private static final int REQUEST_GOOGLE_SIGN_IN =143;
    public static GoogleApiClient mGoogleApiClient,mGoogleApiClient1;

    private Uri uri;

    private SmoothNavigationToggle smoothNavigationToggle;

    private NavigationView navigationView;


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
        final TextView name = (TextView) menuHeaderView.findViewById(R.id.person_name);
        name.setText(String.format(" %s", prefs.getString(getString(R.string.key_user_name), "")));


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        smoothNavigationToggle = new SmoothNavigationToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if((mGoogleApiClient !=null) &&(!(mGoogleApiClient.isConnected()))){

                        name.setText("Welcome "+String.format(" %s", prefs.getString(getString(R.string.key_user_name), "")));

                }

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

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addScope(Drive.SCOPE_APPFOLDER)
                .addOnConnectionFailedListener(this)
                .build();


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();


        mGoogleApiClient1 = new GoogleApiClient.Builder(this)
                .enableAutoManage(this , this )
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();



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
        restoreColorScheme();
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
            case R.id.sign_in:
                Menu menu = navigationView.getMenu();
                MenuItem aaa = menu.getItem(7);
                if(mGoogleApiClient !=null){
                    if("Sign Out".equals(aaa.getTitle())){
                        aaa.setTitle("Sign In");
                        mGoogleApiClient.clearDefaultAccountAndReconnect();
                        mGoogleApiClient.disconnect();
                        Auth.GoogleSignInApi.signOut(mGoogleApiClient1)
                                .setResultCallback(logout);
                    }
                    else {
                        mGoogleApiClient.connect();
                    }
                }
                break;
            default:
                //do nothing
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void restoreColorScheme() {
        int primaryColor = ContextCompat.getColor(HomeActivity.this, R.color.color_primary);
        int primaryColorDark = ContextCompat.getColor(HomeActivity.this, R.color.color_primary_dark);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(primaryColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(primaryColorDark);
            getWindow().setNavigationBarColor(primaryColor);
        }
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


    @Override
    public void onConnected(@Nullable Bundle bundle) {



        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient1);
        startActivityForResult(signInIntent, REQUEST_GOOGLE_SIGN_IN);
    }

    @Override
    public void onConnectionSuspended(int i) {
        if(mGoogleApiClient!=null){
            mGoogleApiClient.disconnect();
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        if (!result.hasResolution()) {
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
            return;
        }
        try {
            result.startResolutionForResult(this, REQUEST_DRIVE_SIGNIN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {

            case REQUEST_DRIVE_SIGNIN:

                if (resultCode == Activity.RESULT_OK) {

                    mGoogleApiClient.connect();

                }
                else if (resultCode == RESULT_CANCELED){

                    Log.d("TAG","result cancelled");
                    return ;
                }
                break;

            case REQUEST_GOOGLE_SIGN_IN:

                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

                if (result.isSuccess()) {

                    String name = result.getSignInAccount().getDisplayName();
                    String email = result.getSignInAccount().getEmail();
                    uri = result.getSignInAccount().getPhotoUrl();
                    TextView personname = (TextView)findViewById(R.id.person_name);
                    Menu menu = navigationView.getMenu();
                    MenuItem aaa = menu.getItem(7);

                    aaa.setTitle("Sign Out");
                    Picasso.with(this).load(uri).transform(new CircleTransform()).into((ImageView)findViewById(R.id.profile_pic));
                    personname.setText("Welcome " + name);
                    Toast.makeText(this,"   connected "+email,Toast.LENGTH_SHORT).show();



                }
                else{

                    Toast.makeText(this,"No internet",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                //do nothing
                break;


        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onResume() {

        super.onResume();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if ((mGoogleApiClient != null)&& (mGoogleApiClient.isConnected())){

            mGoogleApiClient.clearDefaultAccountAndReconnect();
            mGoogleApiClient.disconnect();
        }
        if ((mGoogleApiClient1 != null)&& (mGoogleApiClient1.isConnected())){

            Auth.GoogleSignInApi.signOut(mGoogleApiClient1)
                    .setResultCallback(logout);


        }
    }

    /**
     * logout is result callback defined to logout user from google sign in api
     */

    private ResultCallback<Status> logout= new ResultCallback<Status>() {
        @Override
        public void onResult(@NonNull Status status) {

            ImageView iv = (ImageView) findViewById(R.id.profile_pic);
            Picasso.with(getApplicationContext()).load(logo_70).into(iv);
            TextView personname = (TextView)findViewById(R.id.person_name);
            personname.setText("Welcome");
            personname.setGravity(Gravity.CENTER);

        }
    };
}



