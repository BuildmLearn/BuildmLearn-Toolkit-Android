package org.buildmlearn.toolkit.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.fragment.NavigationDrawerFragment;
import org.buildmlearn.toolkit.fragment.TestFragment;
import org.buildmlearn.toolkit.model.Section;


public class HomeActivity extends ActionBarActivity
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
                    Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
            return;
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
                    Bundle args = new Bundle();
                    args.putString(TestFragment.ARG_PARAM1, "ABC: " + position);
                    args.putString(TestFragment.ARG_PARAM2, "XYZ: " + position);
                    f.setArguments(args);
                    ft.add(R.id.container, f, fragmentClassName);
                }
                ft.commit();


            }
        }
    }


    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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
