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
public class TFTComprehensionFragment extends Fragment {

    public final static String TAG = "QUIZ_FRAGMENT_START";


    private GlobalData gd;
    private View view;

    public static Fragment newInstance(String path) {
        TFTComprehensionFragment fragment = new TFTComprehensionFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SIMULATOR_FILE_PATH, path);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.quiz_template_fragment_start_view, container, false);

        gd = GlobalData.getInstance();
        reInitialize();
        // gd.ReadContent(TFTComprehensionFragment.this);
        gd.readXml(getArguments().getString(Constants.SIMULATOR_FILE_PATH));
        TextView quizAuthor = (TextView) view.findViewById(R.id.tv_author);
        TextView quizTitle = (TextView) view.findViewById(R.id.tv_apptitle);

        quizAuthor.setText(gd.iQuizAuthor);
        quizTitle.setText(gd.iQuizTitle);

        Button startButton = (Button) view.findViewById(R.id.btn_start);
        startButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                getActivity().getFragmentManager().beginTransaction().replace(R.id.container, new QuestionFragment(), QuestionFragment.TAG).addToBackStack(null).commit();
            }
        });

        return view;
    }

    private void reInitialize() {
        gd.total = 0;
        gd.correct = 0;
        gd.wrong = 0;
        gd.iQuizList.clear();
    }

}
