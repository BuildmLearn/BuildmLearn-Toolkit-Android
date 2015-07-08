package org.buildmlearn.toolkit.flashcardtemplate;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;

public class StartFragment extends Fragment {
    GlobalData gd;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.flash_simu_start_view, container, false);

        gd = GlobalData.getInstance();
        gd.readXml(getActivity(), "flash_content.xml");

        TextView quizAuthor = (TextView) view.findViewById(R.id.tv_author);
        TextView quizTitle = (TextView) view.findViewById(R.id.tv_apptitle);

        quizAuthor.setText(gd.iQuizAuthor);
        quizTitle.setText(gd.iQuizTitle);

        Button startButton = (Button) view.findViewById(R.id.btn_start);
        startButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO: Handle this
                Intent myIntent = new Intent(arg0.getContext(),
                        MainFragment.class);
                startActivity(myIntent);
            }
        });

        return view;
    }


}
