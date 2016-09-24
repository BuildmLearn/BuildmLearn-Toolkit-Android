package org.buildmlearn.toolkit.videocollectiontemplate.fragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.videocollectiontemplate.Constants;
import org.buildmlearn.toolkit.videocollectiontemplate.data.DataUtils;
import org.buildmlearn.toolkit.videocollectiontemplate.data.FetchXMLTask;
import org.buildmlearn.toolkit.videocollectiontemplate.data.VideoDb;
import org.buildmlearn.toolkit.views.TextViewPlus;

/**
 * @brief Splash intro Fragment for video collection template's simulator.
 * <p/>
 * Created by Anupam (opticod) on 21/5/16.
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
        ((TextViewPlus) rootView.findViewById(R.id.intro_text)).setText(getResources().getString(R.string.video_collection_title));

        rootView.findViewById(R.id.enter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), MainActivityFragment.newInstance()).addToBackStack(null).commit();
            }
        });

        VideoDb db = new VideoDb(mActivity);
        db.open();
        db.deleteAll();

        long numColumns = db.getCount();
        db.close();
        if (numColumns == 0) {
            FetchXMLTask xmlTask = new FetchXMLTask(getActivity());
            xmlTask.execute(Constants.XMLFileName);
        }
        return rootView;
    }
}
