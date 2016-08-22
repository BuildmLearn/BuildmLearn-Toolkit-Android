package org.buildmlearn.toolkit.quiztemplate.fragment;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.quiztemplate.data.QuizDb;

import java.util.Locale;

/**
 * Created by Anupam (opticod) on 14/8/16.
 */

/**
 * @brief Fragment for displaying score to user in quiz template's simulator.
 */
public class LastFragment extends Fragment {

    private QuizDb db;

    public static Fragment newInstance() {
        return new LastFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.comprehension_fragment_last, container, false);

        db = new QuizDb(getActivity());
        db.open();

        int stat[] = db.getStatistics();

        Toolbar maintoolbar = (Toolbar) rootView.findViewById(R.id.toolbar_main);
        maintoolbar.setTitle(getResources().getString(R.string.app_name_quiz));
        maintoolbar.inflateMenu(R.menu.menu_main_white);

        maintoolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
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

        ((TextView) rootView.findViewById(R.id.text)).setText(getResources().getString(R.string.completed_message_quiz));
        ((TextView) rootView.findViewById(R.id.correct)).setText(String.format(Locale.getDefault(), "Total Correct : %1$d", stat[0]));
        ((TextView) rootView.findViewById(R.id.wrong)).setText(String.format(Locale.getDefault(), "Total Wrong : %1$d", stat[1]));
        ((TextView) rootView.findViewById(R.id.un_answered)).setText(String.format(Locale.getDefault(), "Total Unanswered : %1$d", stat[2]));

        rootView.findViewById(R.id.restart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.resetCount();
                Bundle arguments = new Bundle();
                arguments.putString(Intent.EXTRA_TEXT, "1");

                Fragment frag = QuestionFragment.newInstance();
                frag.setArguments(arguments);
                getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), frag).addToBackStack(null).commit();
            }
        });

        rootView.findViewById(R.id.exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }
}
