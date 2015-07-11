package org.buildmlearn.toolkit.flashcardtemplate;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.constant.Constants;

public class StartFragment extends Fragment {

    public static final String TAG = "Start Fragment";

    GlobalData gd;

    public static Fragment newInstance(String path) {
        StartFragment fragment = new StartFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SIMULATOR_FILE_PATH, path);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.flash_simu_start_view, container, false);

        gd = GlobalData.getInstance();
        gd.readXml(getActivity(), "template_assets/flash_content.xml");

        TextView quizAuthor = (TextView) view.findViewById(R.id.tv_author);
        TextView quizTitle = (TextView) view.findViewById(R.id.tv_apptitle);

        quizAuthor.setText(gd.iQuizAuthor);
        quizTitle.setText(gd.iQuizTitle);

        Button startButton = (Button) view.findViewById(R.id.btn_start);
        startButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                getActivity().getFragmentManager().beginTransaction().replace(R.id.container, new MainFragment()).addToBackStack(null).commit();

            }
        });

        return view;
    }


}
