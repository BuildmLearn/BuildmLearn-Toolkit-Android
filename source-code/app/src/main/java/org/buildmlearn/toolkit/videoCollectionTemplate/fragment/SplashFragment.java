package org.buildmlearn.toolkit.videoCollectionTemplate.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.videoCollectionTemplate.Constants;
import org.buildmlearn.toolkit.videoCollectionTemplate.data.DataUtils;
import org.buildmlearn.toolkit.videoCollectionTemplate.data.FetchXMLTask;
import org.buildmlearn.toolkit.videoCollectionTemplate.data.VideoDb;

/**
 * Created by Anupam (opticod) on 21/5/16.
 */
public class SplashFragment extends Fragment {

    public SplashFragment() {

    }

    public static Fragment newInstance(String path) {
        SplashFragment fragment = new SplashFragment();
        Constants.XMLFileName = path;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_splash_video, container, false);

        final Activity mActivity = getActivity();
        final String result[] = DataUtils.read_Title_Author();
        TextView title = (TextView) rootView.findViewById(R.id.title);
        TextView author_name = (TextView) rootView.findViewById(R.id.author_name);

        title.setText(result[0]);
        author_name.setText(result[1]);

        rootView.findViewById(R.id.enter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), MainActivityFragment.newInstance()).addToBackStack(null).commit();
            }
        });

        VideoDb db = new VideoDb(mActivity);
        db.open();
        db.deleteAll();

        long numColumns = db.getCount();
        if (numColumns == 0) {
            FetchXMLTask xmlTask = new FetchXMLTask(getActivity());
            xmlTask.execute(Constants.XMLFileName);
        }
        return rootView;
    }
}
