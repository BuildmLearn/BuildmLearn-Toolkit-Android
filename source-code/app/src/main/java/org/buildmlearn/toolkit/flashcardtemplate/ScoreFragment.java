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

public class ScoreFragment extends Fragment {
    GlobalData gd;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.flash_simu_finish, container, false);
        gd = GlobalData.getInstance();
        TextView mCardQuizName = (TextView) view.findViewById(R.id.tv_lastcard);
        mCardQuizName.setText(gd.iQuizTitle);

        Button startAgainButton = (Button) view.findViewById(R.id.btn_restart);
        startAgainButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent myIntent = new Intent(getActivity(),
                        StartFragment.class);
                startActivity(myIntent);
            }
        });

        Button quitButton = (Button) view.findViewById(R.id.btn_exit);
        quitButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        return view;
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

    }


}
