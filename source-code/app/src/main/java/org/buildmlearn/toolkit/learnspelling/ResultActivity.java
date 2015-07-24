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

package org.buildmlearn.toolkit.learnspelling;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.constant.Constants;
import org.buildmlearn.toolkit.flashcardtemplate.StartFragment;

import java.util.Locale;

public class ResultActivity extends Fragment {
    private TextView mTv_Correct, mTv_Wrong, mTv_Unanswered;
    private DataManager mDataManager;
    private TextToSpeech textToSpeech;
    private int unanswered, wrong, correct;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.spelling_fragment_finish, container, false);
        mDataManager = DataManager.getInstance();
        mTv_Correct = (TextView) view.findViewById(R.id.tv_correct);
        mTv_Wrong = (TextView) view.findViewById(R.id.tv_wrong);
        mTv_Unanswered = (TextView) view.findViewById(R.id.tv_unanswered);
        correct = mDataManager.getCorrect();
        wrong = mDataManager.getWrong();
        unanswered = mDataManager.getList().size() - correct - wrong;

        mTv_Correct.setText(getString(R.string.correct) + " " + correct);

        mTv_Wrong.setText(getString(R.string.wrong_spelled) + " " + wrong);
        mTv_Unanswered.setText(getString(R.string.unanswered) + " "
                + unanswered);
        textToSpeech = new TextToSpeech(getActivity(),
                new TextToSpeech.OnInitListener() {

                    @Override
                    public void onInit(int arg0) {
                        if (arg0 == TextToSpeech.SUCCESS) {
                            textToSpeech.setLanguage(Locale.US);
                            String speechText = getString(R.string.correct)
                                    + " " + correct
                                    + getString(R.string.wrong_spelled) + " "
                                    + wrong + getString(R.string.unanswered)
                                    + " " + unanswered;
                            convertTextToSpeech(speechText);
                        }
                    }
                });


        view.findViewById(R.id.btn_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        view.findViewById(R.id.btn_restart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDataManager.reset();
                getActivity().getFragmentManager().beginTransaction().replace(R.id.container, SpellingMainFragment.newInstance(getActivity().getIntent().getStringExtra(Constants.SIMULATOR_FILE_PATH)), StartFragment.TAG).addToBackStack(null).commit();
//t
            }
        });

        return view;
    }


    @Override
    public void onStop() {
        super.onStop();
        textToSpeech.shutdown();
    }

    private void convertTextToSpeech(String text) {

        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

}
