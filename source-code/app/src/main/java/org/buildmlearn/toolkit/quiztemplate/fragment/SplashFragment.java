package org.buildmlearn.toolkit.quiztemplate.fragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.quiztemplate.Constants;
import org.buildmlearn.toolkit.quiztemplate.data.DataUtils;
import org.buildmlearn.toolkit.quiztemplate.data.FetchXMLTask;
import org.buildmlearn.toolkit.quiztemplate.data.QuizDb;
import org.buildmlearn.toolkit.views.TextViewPlus;

/**
 * Created by Anupam (opticod) on 14/8/16.
 */

/**
 * @brief Splash intro Fragment for quiz template's simulator.
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
        ((TextViewPlus) rootView.findViewById(R.id.intro_text)).setText(getResources().getString(R.string.app_name_quiz));


        rootView.findViewById(R.id.enter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle arguments = new Bundle();
                arguments.putString(Intent.EXTRA_TEXT, "1");

                Fragment frag = QuestionFragment.newInstance();
                frag.setArguments(arguments);
                getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getActivity().getSupportFragmentManager().beginTransaction().replace(((ViewGroup) getView().getParent()).getId(), frag).addToBackStack(null).commit();

            }
        });

        QuizDb db = new QuizDb(mActivity);
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
