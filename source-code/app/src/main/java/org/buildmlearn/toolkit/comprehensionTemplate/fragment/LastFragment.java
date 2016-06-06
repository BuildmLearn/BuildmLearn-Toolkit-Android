package org.buildmlearn.toolkit.comprehensionTemplate.fragment;

import android.app.FragmentManager;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.comprehensionTemplate.data.ComprehensionDb;

import java.util.Locale;

/**
 * Created by Anupam (opticod) on 5/6/16.
 */
public class LastFragment extends Fragment {

    public LastFragment() {

    }

    public static Fragment newInstance() {
        return new LastFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.comprehension_fragment_last, container, false);

        final ComprehensionDb db = new ComprehensionDb(getActivity());
        db.open();

        Cursor cursor = db.getMetaCursor();
        cursor.moveToFirst();

        int stat[] = db.getStatistics();
        db.close();

        ((TextView) rootView.findViewById(R.id.correct)).setText(String.format(Locale.getDefault(), "Total Correct : %1$d", stat[0]));
        ((TextView) rootView.findViewById(R.id.wrong)).setText(String.format(Locale.getDefault(), "Total Wrong : %1$d", stat[1]));
        ((TextView) rootView.findViewById(R.id.un_answered)).setText(String.format(Locale.getDefault(), "Total Unanswered : %1$d", stat[2]));

        rootView.findViewById(R.id.restart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle arguments = new Bundle();
                arguments.putString(Intent.EXTRA_TEXT, "1");

                Fragment frag = MainFragment.newInstance();
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
}
