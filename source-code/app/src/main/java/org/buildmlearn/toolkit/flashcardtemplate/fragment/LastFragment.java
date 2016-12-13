package org.buildmlearn.toolkit.flashcardtemplate.fragment;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;

/**
 * Created by anupam on 10/8/16.
 */

/**
 * @brief Fragment for displaying score to user in flash card template's simulator.
 */
public class LastFragment extends Fragment {

    public static Fragment newInstance() {
        return new LastFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_last_flash, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar_main);
        toolbar.setTitle(getResources().getString(R.string.main_title_flash));
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
