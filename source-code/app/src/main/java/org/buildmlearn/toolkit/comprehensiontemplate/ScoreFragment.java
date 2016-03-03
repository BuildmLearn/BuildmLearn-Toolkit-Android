/* Copyright (c) 2012, BuildmLearn Contributors listed at http://buildmlearn.org/people/
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice,
  this list of conditions and the following disclaimer in the documentation
  and/or other materials provided with the distribution.

* Neither the name of the BuildmLearn nor the names of its
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */


package org.buildmlearn.toolkit.comprehensiontemplate;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.constant.Constants;

/**
 * @brief Simulator code for Quiz Template
 */
public class ScoreFragment extends Fragment {

    public final static String TAG = "SCORE_FRAGMENT";

    private GlobalData gd;
    private TextView mTv_correct, mTv_wrong, mTv_unanswered;
    private FragmentActivity faActivity;
    private View view;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        faActivity = (FragmentActivity) super.getActivity();
        view = inflater.inflate(R.layout.quiz_template_fragment_score_view, container, false);

        gd = GlobalData.getInstance();

        mTv_correct = (TextView) view.findViewById(R.id.tv_correct);
        mTv_wrong = (TextView) view.findViewById(R.id.tv_wrong);
        mTv_unanswered = (TextView) view.findViewById(R.id.tv_unanswered);
        mTv_correct.setText("Total Correct: " + gd.correct);
        mTv_wrong.setText("Total Wrong: " + gd.wrong);
        int unanswered = gd.total - gd.correct - gd.wrong;
        mTv_unanswered.setText("Unanswered: " + unanswered);

        Button startAgainButton = (Button) view.findViewById(R.id.start_again_button);
        startAgainButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                getActivity().getFragmentManager().beginTransaction().replace(R.id.container, TFTComprehensionFragment.newInstance(getActivity().getIntent().getStringExtra(Constants.SIMULATOR_FILE_PATH)), TFTComprehensionFragment.TAG).addToBackStack(null).commit();
            }
        });

        Button quitButton = (Button) view.findViewById(R.id.quit_button);
        quitButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                getActivity().finish();
            }
        });

        return view;
    }
}
