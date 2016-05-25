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

import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;

import java.util.ArrayList;
import java.util.Locale;

/**
 * @brief Simulator code for Learn Spelling Template
 */
public class WordInfoActivity extends Fragment {

    private boolean isCorrect;
    private int position;
    private DataManager mManager;
    private ArrayList<WordModel> mList;
    private TextToSpeech textToSpeech;

    public static Fragment newInstance(boolean isCorrect, int count, String word) {
        Fragment fragment = new WordInfoActivity();
        Bundle bundle = new Bundle();
        bundle.putBoolean("result", isCorrect);
        bundle.putInt("index", count);
        bundle.putString("word", word);

        fragment.setArguments(bundle);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.spelling_fragment_word_info, container, false);
        mManager = DataManager.getInstance();
        mList = mManager.getList();

        isCorrect = getArguments().getBoolean("result", false);
        position = getArguments().getInt("index", 0);
        String enteredText = getArguments().getString("word");
        TextView mTv_Result = (TextView) view.findViewById(R.id.tv_result);
        TextView mTv_Word_num = (TextView) view.findViewById(R.id.tv_word_num);
        TextView mTv_word = (TextView) view.findViewById(R.id.tv_word);
        TextView mTv_enteredWord = (TextView) view.findViewById(R.id.tv_input_word);

        TextView mTv_description = (TextView) view.findViewById(R.id.tv_description);
        Button mBtn_Next = (Button) view.findViewById(R.id.btn_next);
        if (position == mList.size() - 1) {
            mBtn_Next.setText("Finish");
        }
        if (isCorrect) {
            mTv_Result.setText(getString(R.string.msg_successful));
            mTv_Result.setTextColor(Color.GREEN);
            mTv_enteredWord.setVisibility(View.GONE);
        } else {
            mTv_Result.setText(getString(R.string.msg_failure));
            mTv_Result.setTextColor(Color.RED);
            mTv_enteredWord.setText(getString(R.string.you_entered) + " "
                    + enteredText.toLowerCase());
        }
        textToSpeech = new TextToSpeech(getActivity(),
                new TextToSpeech.OnInitListener() {

                    @Override
                    public void onInit(int arg0) {
                        if (arg0 == TextToSpeech.SUCCESS) {
                            textToSpeech.setLanguage(Locale.US);
                            if (isCorrect)
                                convertTextToSpeech(getString(R.string.msg_successful));
                            else
                                convertTextToSpeech(getString(R.string.msg_failure));
                        }
                    }
                });
        mTv_Word_num.setText("Word #" + (position + 1) + " of " + mList.size());
        mTv_word.setText(mList.get(position).getWord().toLowerCase());
        mTv_description.setText(mList.get(position).getDescription());


        view.findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position < mList.size() - 1) {
                    mManager.increaseCount();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new SpellingActivity()).addToBackStack(null).commit();
                } else {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, new ResultActivity()).addToBackStack(null).commit();
                }
            }
        });
        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        textToSpeech.shutdown();
    }

    private void convertTextToSpeech(String text) {

        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}
