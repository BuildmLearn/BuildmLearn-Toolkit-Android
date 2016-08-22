package org.buildmlearn.toolkit.learnspelling.fragment;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.learnspelling.Constants;
import org.buildmlearn.toolkit.learnspelling.data.SpellDb;

import java.util.Locale;

/**
 * Created by Anupam (opticod) on 31/5/16.
 */

/**
 * @brief Fragment for displaying the correct and user entered spell to users in spelling template's simulator.
 */

public class ResponseFragment extends Fragment
        implements NavigationView.OnNavigationItemSelectedListener {

    private SpellDb db;
    private View rootView;

    public static Fragment newInstance() {
        return new ResponseFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_response_spell, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary_comprehension));
        toolbar.inflateMenu(R.menu.menu_main_white);
        toolbar.setTitle(getResources().getString(R.string.main_title_spell));

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_about:
                        AlertDialog.Builder builder =
                                new AlertDialog.Builder(getActivity());
                        builder.setTitle(String.format("%1$s", getString(R.string.comprehension_about_us)));
                        builder.setMessage(getResources().getText(R.string.comprehension_about_text));
                        builder.setPositiveButton("OK", null);
                        AlertDialog welcomeAlert = builder.create();
                        welcomeAlert.show();
                        assert welcomeAlert.findViewById(android.R.id.message) != null;
                        assert welcomeAlert.findViewById(android.R.id.message) != null;
                        assert ((TextView) welcomeAlert.findViewById(android.R.id.message)) != null;
                        ((TextView) welcomeAlert.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
                        break;
                    default: //do nothing
                        break;
                }
                return true;
            }
        });

        DrawerLayout drawer = (DrawerLayout) rootView.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) rootView.findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

        Context mContext = getActivity();

        db = new SpellDb(mContext);
        db.open();


        Bundle extras = getArguments();
        String spellId = "1";
        if (extras != null) {
            spellId = extras.getString(Intent.EXTRA_TEXT);
        }

        Cursor spell_cursor = db.getSpellingCursorById(Integer.parseInt(spellId));
        spell_cursor.moveToFirst();
        String word = spell_cursor.getString(Constants.COL_WORD);
        String meaning = spell_cursor.getString(Constants.COL_MEANING);
        String answered = spell_cursor.getString(Constants.COL_ANSWERED);


        Menu m = navigationView.getMenu();
        SubMenu topChannelMenu = m.addSubMenu("Spellings");
        long numQues = db.getCountSpellings();

        for (int i = 1; i <= numQues; i++) {
            topChannelMenu.add(String.format(Locale.getDefault(), "Spelling %1$d", i));
            topChannelMenu.getItem(i - 1).setIcon(R.drawable.ic_assignment_black_24dp);
            final int finalI = i;
            topChannelMenu.getItem(i - 1).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    Bundle arguments = new Bundle();
                    arguments.putString(Intent.EXTRA_TEXT, String.valueOf(finalI));

                    Fragment frag = MainFragment.newInstance();
                    frag.setArguments(arguments);
                    getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), frag).addToBackStack(null).commit();

                    return false;
                }
            });
        }

        MenuItem mi = m.getItem(m.size() - 1);
        mi.setTitle(mi.getTitle());

        TextView mTv_WordNumber = (TextView) rootView.findViewById(R.id.intro_number);

        mTv_WordNumber.setText(String.format(Locale.ENGLISH, "Word #%d of %d", Integer.parseInt(spellId), numQues));

        String message;
        String word_text_view;
        if (word.trim().equalsIgnoreCase(answered)) {
            message = "Great! You got it right.";
            word_text_view = "Correct Spell: &nbsp<font color='green'>" + word + "</font>";
        } else {
            message = "Oops! You got it wrong.";
            word_text_view = "Correct Spell:&nbsp <font color=\"#7fe77f\">" + word + "</font> <br />You entered:&nbsp <font color=\"#ee9797\">" + answered + "</font>";
        }

        assert rootView.findViewById(R.id.intro_response) != null;
        ((TextView) rootView.findViewById(R.id.intro_response)).setText(message);
        assert rootView.findViewById(R.id.word) != null;
        ((TextView) rootView.findViewById(R.id.word)).setText(Html.fromHtml(word_text_view), TextView.BufferType.SPANNABLE);

        assert rootView.findViewById(R.id.meaning) != null;
        ((TextView) rootView.findViewById(R.id.meaning)).setText(meaning);

        final String finalSpellId = spellId;
        rootView.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long numColumns = db.getCountSpellings();

                long nextSpellId = Integer.parseInt(finalSpellId) + 1;

                if (nextSpellId <= numColumns) {

                    Bundle arguments = new Bundle();
                    arguments.putString(Intent.EXTRA_TEXT, String.valueOf(nextSpellId));

                    Fragment frag = MainFragment.newInstance();
                    frag.setArguments(arguments);

                    getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), frag).addToBackStack(null).commit();

                } else {
                    Bundle arguments = new Bundle();
                    arguments.putString(Intent.EXTRA_TEXT, String.valueOf(nextSpellId));

                    Fragment frag = LastFragment.newInstance();
                    frag.setArguments(arguments);

                    getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), frag).addToBackStack(null).commit();

                }
            }
        });
        return rootView;
    }

    @Override
    public void onDestroy() {
        db.close();
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) rootView.findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
