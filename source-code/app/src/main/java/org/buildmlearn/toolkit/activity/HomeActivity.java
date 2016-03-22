package org.buildmlearn.toolkit.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.fragment.NavigationDrawerFragment;
import org.buildmlearn.toolkit.model.Section;

import io.fabric.sdk.android.Fabric;

/**
 * @brief Home screen of the application containing all the menus and settings.
 */
public class HomeActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private Section currentSection;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_home);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        Section[] menuItem = Section.values();
        Section selectedMenuItem = menuItem[position];
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
                FragmentTransaction ft = fm.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
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


    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


}
