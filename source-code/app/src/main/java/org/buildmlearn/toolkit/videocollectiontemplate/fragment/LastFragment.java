package org.buildmlearn.toolkit.videocollectiontemplate.fragment;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.buildmlearn.toolkit.R;

/**
 * @brief Fragment for displaying score to user in video collection template's simulator.
 * <p/>
 * Created by Anupam (opticod) on 21/5/16.
 */
public class LastFragment extends Fragment {

    public static Fragment newInstance() {
        return new LastFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_last_video, container, false);

        rootView.findViewById(R.id.restart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle arguments = new Bundle();
                arguments.putString(Intent.EXTRA_TEXT, "1");

                Fragment frag = DetailActivityFragment.newInstance();
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

        Toolbar maintoolbar = (Toolbar) rootView.findViewById(R.id.toolbar_main);
        maintoolbar.setTitle(getString(R.string.video_collection_title));
        maintoolbar.setNavigationIcon(R.drawable.ic_home_white_24dp);

        maintoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), MainActivityFragment.newInstance()).addToBackStack(null).commit();
            }
        });


        return rootView;
    }
}
