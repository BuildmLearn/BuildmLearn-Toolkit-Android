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

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.buildmlearn.toolkit.R;

import java.util.ArrayList;
import java.util.Locale;

public class SpellingActivity extends Fragment implements
        TextToSpeech.OnInitListener {
    private TextToSpeech textToSpeech;
    private DataManager mManager;
    private ArrayList<WordModel> mWordList;
    private int count;
    private AlertDialog mAlert;
    private TextView mTv_WordNumber;
    private Button mBtn_Spell, mBtn_Skip;
    private EditText mEt_Spelling;
    private SeekBar mSb_SpeechRate;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.spelling_fragment_spelling, container, false);

        mBtn_Spell = (Button) view.findViewById(R.id.btn_ready);

        mBtn_Skip = (Button) view.findViewById(R.id.btn_skip);
        mSb_SpeechRate = (SeekBar) view.findViewById(R.id.sb_speech);

        mTv_WordNumber = (TextView) view.findViewById(R.id.tv_word_number);
        textToSpeech = new TextToSpeech(getActivity(), this);
        mManager = DataManager.getInstance();
        mWordList = mManager.getList();
        count = mManager.getActiveWordCount();
        mTv_WordNumber.setText("Word #" + (count + 1) + " of "
                + mWordList.size());

        view.findViewById(R.id.btn_skip).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (count < mWordList.size() - 1) {

                    count++;
                    mManager.increaseCount();
                    mTv_WordNumber.setText("Word #" + (count + 1) + " of "
                            + mWordList.size());
                    mBtn_Spell.setEnabled(false);
                    mBtn_Skip.setEnabled(false);
                    mBtn_Skip.setTextColor(Color.WHITE);
                    mBtn_Spell.setTextColor(Color.WHITE);
                } else {
                    getActivity().getFragmentManager().beginTransaction().replace(R.id.container, new ResultActivity()).addToBackStack(null).commit();
                }
            }
        });
        view.findViewById(R.id.btn_speak).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                convertTextToSpeech(mWordList.get(count).getWord());
                mBtn_Spell.setEnabled(true);
                mBtn_Skip.setEnabled(true);
                mBtn_Skip.setTextColor(Color.RED);
                mBtn_Spell.setTextColor(Color.GREEN);
            }
        });
        view.findViewById(R.id.btn_ready).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater factory = LayoutInflater.from(getActivity());
                final View textEntryView = factory.inflate(
                        R.layout.spelling_dialog_spellinginput, null);
                Builder builder = new Builder(getActivity());
                mAlert = builder.create();
                mAlert.setCancelable(true);
                mAlert.setView(textEntryView, 10, 10, 10, 10);
                if (mAlert != null && !mAlert.isShowing()) {
                    mAlert.show();
                }
                mEt_Spelling = (EditText) mAlert.findViewById(R.id.et_spelling);
                Button mBtn_Submit = (Button) mAlert.findViewById(R.id.btn_submit);
                mBtn_Submit.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        submit();
                    }
                });
            }
        });

        return view;
    }


    /**
     * Releases the resources used by the TextToSpeech engine. It is good
     * practice for instance to call this method in the onDestroy() method of an
     * Activity so the TextToSpeech engine can be cleanly stopped.
     *
     * @see android.app.Activity#onDestroy()
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        textToSpeech.shutdown();
    }

    /**
     * Speaks the string using the specified queuing strategy and speech
     * parameters.
     */
    private void convertTextToSpeech(String text) {

        float speechRate = getProgressValue(mSb_SpeechRate.getProgress());
        textToSpeech.setSpeechRate(speechRate);
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);

    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("error", "This Language is not supported");
            }
        } else {
            Log.e("error", "Initilization Failed!");
        }
    }

    public void submit() {
        String input = mEt_Spelling.getText().toString().trim();
        if (input == null || input.length() == 0) {
            Toast.makeText(getActivity(), "Please enter the spelling",
                    Toast.LENGTH_SHORT).show();

        } else {
            mAlert.dismiss();
            boolean isCorrect = false;
            if (mEt_Spelling.getText().toString()
                    .equalsIgnoreCase(mWordList.get(count).getWord())) {
                isCorrect = true;
                mManager.incrementCorrect();
            } else {
                mManager.incrementWrong();
            }

            getActivity().getFragmentManager().beginTransaction().replace(R.id.container, WordInfoActivity.newInstance(isCorrect, count, input)).addToBackStack(null).commit();

        }
    }

    private float getProgressValue(int percent) {
        float temp = ((float) percent / 100);
        float per = temp * 2;
        return per;
    }


}
