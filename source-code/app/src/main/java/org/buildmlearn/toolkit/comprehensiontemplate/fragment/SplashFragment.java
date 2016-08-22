package org.buildmlearn.toolkit.comprehensiontemplate.fragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.comprehensiontemplate.Constants;
import org.buildmlearn.toolkit.comprehensiontemplate.data.ComprehensionDb;
import org.buildmlearn.toolkit.comprehensiontemplate.data.DataUtils;
import org.buildmlearn.toolkit.comprehensiontemplate.data.FetchXMLTask;
import org.buildmlearn.toolkit.views.TextViewPlus;

/**
 * Created by Anupam (opticod) on 5/6/16.
 */

/**
 * @brief Splash intro Fragment for comprehension template's simulator.
 */
public class SplashFragment extends Fragment {

    public static Fragment newInstance(String path) {
        SplashFragment fragment = new SplashFragment();
        Constants.XMLFileName = path;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_splash, container, false);

        final Activity mActivity = getActivity();
        final String result[] = DataUtils.readTitleAuthor();
        TextView title = (TextView) rootView.findViewById(R.id.title);
        TextView author_name = (TextView) rootView.findViewById(R.id.author_name);

        title.setText(result[0]);
        author_name.setText(result[1]);
        ((TextViewPlus) rootView.findViewById(R.id.intro_text)).setText(getResources().getString(R.string.comprehension_title));


        rootView.findViewById(R.id.enter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), MainFragment.newInstance()).addToBackStack(null).commit();
            }
        });

        ComprehensionDb db = new ComprehensionDb(mActivity);
        db.open();
        db.deleteAll();

        long numColumns = db.getCountQuestions();
        db.close();
        if (numColumns == 0) {
            FetchXMLTask xmlTask = new FetchXMLTask(getActivity());
            xmlTask.execute(Constants.XMLFileName);
        }
        return rootView;
    }
}
