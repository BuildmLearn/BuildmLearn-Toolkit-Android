package org.buildmlearn.toolkit.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.constant.Constants;
import org.buildmlearn.toolkit.fragment.NavigationDrawerFragment;
import org.buildmlearn.toolkit.fragment.SettingsFragment;
import org.buildmlearn.toolkit.model.Section;

/**
 * @brief Home screen of the application containg all the menus and settings.
 */
public class HomeActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private Section currentSection;
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_home);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        /*
      Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        if (getIntent().hasExtra(Constants.START_FRAGMENT)) {
            if (getIntent().getIntExtra(Constants.START_FRAGMENT, 0) == 3) {
                currentSection.setIsSelected(false);
                Section[] menuItem = Section.values();
                Section selectedMenuItem = menuItem[3];
                selectedMenuItem.setIsSelected(true);
                currentSection = selectedMenuItem;
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.container, new SettingsFragment());
                ft.commit();
            }
        }
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Section[] menuItem = Section.values();
        Section selectedMenuItem = menuItem[position];
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowCustomEnabled(false);
        }
        if (selectedMenuItem.getType() == Section.ACTIVITY) {
            Class<?> c;
            if (selectedMenuItem.getViewName() != null) {
                try {
                    c = Class.forName(selectedMenuItem.getViewName());
                    Intent intent = new Intent(this, c);
                    startActivity(intent);

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else if (selectedMenuItem.getType() == Section.FRAGMENT) {
            if (currentSection == null || selectedMenuItem != currentSection) {
                currentSection = selectedMenuItem;
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(null);
                Fragment f = fm.findFragmentById(R.id.container);
                if (f != null) {
                    if (currentSection.isKeep()) {
                        ft.detach(f);
                    } else {
                        ft.remove(f);
                    }
                }
                String fragmentClassName = currentSection.getViewName();
                if (currentSection.isKeep() && ((f = fm.findFragmentByTag(fragmentClassName)) != null)) {
                    ft.attach(f);
                } else {
                    f = Fragment.instantiate(this, fragmentClassName);
                    ft.add(R.id.container, f, fragmentClassName);
                }
                ft.commit();


            }
        }
    }

    @Override
    public void onBackPressed() {

        if (mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.closeDrawer();
            return;
        }
        if (getFragmentManager().getBackStackEntryCount() <= 1) {
            finish();
        }
        super.onBackPressed();
    }
}
