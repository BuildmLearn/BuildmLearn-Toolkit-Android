package org.buildmlearn.toolkit.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.activity.TemplateActivity;

/**
 * @brief Fragment displayed on the home screen.
 */
public class HomeFragment extends Fragment {

    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        view.findViewById(R.id.button_template).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), TemplateActivity.class));
            }
        });
        return view;
    }


}
