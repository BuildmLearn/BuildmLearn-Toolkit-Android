package org.buildmlearn.toolkit.quiztemplate.fragment;

import android.app.FragmentManager;
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
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.quiztemplate.Constants;
import org.buildmlearn.toolkit.quiztemplate.data.QuizDb;

import java.util.Locale;

/**
 * Created by Anupam (opticod) on 14/8/16.
 */

/**
 * @brief Question Fragment for quiz template's simulator.
 */
public class QuestionFragment extends Fragment
        implements NavigationView.OnNavigationItemSelectedListener {

    private View rootView;
    private QuizDb db;

    public static Fragment newInstance() {
        return new QuestionFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.comprehension_fragment_question, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary_comprehension));
        toolbar.inflateMenu(R.menu.menu_main_white);

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
                getActivity(), drawer, toolbar, R.string.comprehension_navigation_drawer_open, R.string.comprehension_navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) rootView.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Bundle arguments = getArguments();
        String questionId = null;
        if (arguments != null) {
            questionId = arguments.getString(Intent.EXTRA_TEXT);
        }

        db = new QuizDb(getActivity());
        db.open();
        Cursor cursor = db.getQuestionCursorById(Integer.parseInt(questionId));
        cursor.moveToFirst();
        String question = cursor.getString(Constants.COL_QUESTION);
        String option_1 = cursor.getString(Constants.COL_OPTION_1);
        String option_2 = cursor.getString(Constants.COL_OPTION_2);
        String option_3 = cursor.getString(Constants.COL_OPTION_3);
        String option_4 = cursor.getString(Constants.COL_OPTION_4);
        int attempted = cursor.getInt(Constants.COL_ATTEMPTED);
        String answered;

        toolbar.setTitle(getResources().getString(R.string.app_name_quiz));

        Menu m = navigationView.getMenu();
        SubMenu topChannelMenu = m.addSubMenu("Questions");
        long numQues = db.getCountQuestions();

        final String finalQuestionId = questionId;

        final RadioGroup rg = (RadioGroup) rootView.findViewById(R.id.radio_group);

        if (attempted == 1) {
            answered = cursor.getString(Constants.COL_ANSWERED);
            rg.check(rg.getChildAt(Integer.parseInt(answered)).getId());
        }
        for (int i = 1; i <= numQues; i++) {
            topChannelMenu.add(String.format(Locale.getDefault(), "Question %1$d", i));
            topChannelMenu.getItem(i - 1).setIcon(R.drawable.ic_assignment_black_24dp);
            final int finalI = i;
            topChannelMenu.getItem(i - 1).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    int radioButtonID = rg.getCheckedRadioButtonId();
                    View radioButton = rg.findViewById(radioButtonID);
                    int idx = rg.indexOfChild(radioButton);

                    if (idx == -1) {
                        db.markUnAnswered(Integer.parseInt(finalQuestionId));
                    } else {
                        db.markAnswered(Integer.parseInt(finalQuestionId), idx);
                    }

                    Bundle arguments = new Bundle();
                    arguments.putString(Intent.EXTRA_TEXT, String.valueOf(finalI));

                    Fragment frag = QuestionFragment.newInstance();
                    frag.setArguments(arguments);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), frag).addToBackStack(null).commit();

                    return false;
                }
            });
        }

        ((TextView) rootView.findViewById(R.id.question_title)).setText(String.format(Locale.getDefault(), "Question No : %1$s", questionId));
        ((TextView) rootView.findViewById(R.id.question)).setText(question);
        if (option_1 != null) {
            rootView.findViewById(R.id.radioButton1).setVisibility(View.VISIBLE);
            ((TextView) rootView.findViewById(R.id.radioButton1)).setText(option_1);
        }
        if (option_2 != null) {
            rootView.findViewById(R.id.radioButton2).setVisibility(View.VISIBLE);
            ((TextView) rootView.findViewById(R.id.radioButton2)).setText(option_2);
        }
        if (option_3 != null) {
            rootView.findViewById(R.id.radioButton3).setVisibility(View.VISIBLE);
            ((TextView) rootView.findViewById(R.id.radioButton3)).setText(option_3);
        }
        if (option_4 != null) {
            rootView.findViewById(R.id.radioButton4).setVisibility(View.VISIBLE);
            ((TextView) rootView.findViewById(R.id.radioButton4)).setText(option_4);
        }


        rootView.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int radioButtonID = rg.getCheckedRadioButtonId();
                View radioButton = rg.findViewById(radioButtonID);
                int idx = rg.indexOfChild(radioButton);

                if (idx == -1) {
                    db.markUnAnswered(Integer.parseInt(finalQuestionId));
                } else {
                    db.markAnswered(Integer.parseInt(finalQuestionId), idx);
                }

                long numColumns = db.getCountQuestions();

                long nextQuesId = Integer.parseInt(finalQuestionId) + 1;

                if (nextQuesId <= numColumns) {
                    Bundle arguments = new Bundle();
                    arguments.putString(Intent.EXTRA_TEXT, String.valueOf(nextQuesId));

                    Fragment frag = QuestionFragment.newInstance();
                    frag.setArguments(arguments);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), frag).addToBackStack(null).commit();

                } else {

                    Fragment frag = LastFragment.newInstance();
                    getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), frag).addToBackStack(null).commit();

                }

            }
        });

        if (Integer.parseInt(questionId) == 1) {

            rootView.findViewById(R.id.previous).setVisibility(View.INVISIBLE);

        } else {

            rootView.findViewById(R.id.previous).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int radioButtonID = rg.getCheckedRadioButtonId();
                    View radioButton = rg.findViewById(radioButtonID);
                    int idx = rg.indexOfChild(radioButton);

                    if (idx == -1) {
                        db.markUnAnswered(Integer.parseInt(finalQuestionId));
                    } else {
                        db.markAnswered(Integer.parseInt(finalQuestionId), idx);
                    }

                    int prevQuesId = Integer.parseInt(finalQuestionId) - 1;

                    if (prevQuesId >= 1) {

                        Bundle arguments = new Bundle();
                        arguments.putString(Intent.EXTRA_TEXT, String.valueOf(prevQuesId));

                        Fragment frag = QuestionFragment.newInstance();
                        frag.setArguments(arguments);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), frag).addToBackStack(null).commit();

                    }
                }
            });
        }
        return rootView;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        DrawerLayout drawer = (DrawerLayout) rootView.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        db.close();
    }
}
