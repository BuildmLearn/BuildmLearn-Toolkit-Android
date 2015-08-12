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

package org.buildmlearn.toolkit.quiztemplate;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.buildmlearn.toolkit.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @brief Simulator code for Quiz Template
 */
public class QuestionFragment extends Fragment {

    public final static String TAG = "QUESTION_FRAGMENT";

    private GlobalData gd;
    private TextView iQuestion_no_Label;
    private TextView iQuestionLabel;
    private RadioButton iRad1, iRad2, iRad3, iRad0;
    private Button iSubmitButton, iNextButton;
    private List<RadioButton> iRadButtonList = new ArrayList<RadioButton>();
    private int iQuestionIndex = 0;
    private int iCurrentCorrectAnswer;
    private RadioGroup iRadioGroup;
    private FragmentActivity faActivity;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        faActivity = (FragmentActivity) super.getActivity();
        view = inflater.inflate(R.layout.quiz_template_fragment_questions_view, container, false);

        gd = GlobalData.getInstance();

        iQuestion_no_Label = (TextView) view.findViewById(R.id.question_no);
        iQuestionLabel = (TextView) view.findViewById(R.id.question_label);

        iRad0 = (RadioButton) view.findViewById(R.id.radio0);
        iRad1 = (RadioButton) view.findViewById(R.id.radio1);
        iRad2 = (RadioButton) view.findViewById(R.id.radio2);
        iRad3 = (RadioButton) view.findViewById(R.id.radio3);

        iRadioGroup = (RadioGroup) view.findViewById(R.id.radioGroup1);

        iRadButtonList.add(iRad0);
        iRadButtonList.add(iRad1);
        iRadButtonList.add(iRad2);
        iRadButtonList.add(iRad3);

        iSubmitButton = (Button) view.findViewById(R.id.submit_button);
        iSubmitButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                int selectedAnswer = getSelectedAnswer();
                if (selectedAnswer == -1) {
                    Toast.makeText(getActivity(),
                            "Please select an answer!", Toast.LENGTH_LONG).show();
                } else if (selectedAnswer != -1
                        && selectedAnswer == iCurrentCorrectAnswer) {
                    iRadButtonList.get(iCurrentCorrectAnswer)
                            .setBackgroundColor(Color.GREEN);
                    Toast.makeText(getActivity(),
                            "That's the correct answer!", Toast.LENGTH_LONG).show();
                    gd.correct++;
                    iSubmitButton.setEnabled(false);

                } else {
                    iRadButtonList.get(selectedAnswer).setBackgroundColor(
                            Color.RED);
                    iRadButtonList.get(iCurrentCorrectAnswer)
                            .setBackgroundColor(Color.GREEN);
                    Toast.makeText(getActivity(),
                            "Sorry, wrong answer!", Toast.LENGTH_LONG).show();

                    iSubmitButton.setEnabled(false);
                    gd.wrong++;
                    // iSubmitButton.setVisibility(View.GONE);
                    // iNextButton.setVisibility(View.VISIBLE);

                }
            }
        });

        iNextButton = (Button) view.findViewById(R.id.next_button);
        iNextButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                // set all radios to white
                for (int i = 0; i < iRadButtonList.size(); i++) {
                    iRadButtonList.get(i).setBackgroundColor(Color.TRANSPARENT);
                }

                // Increase the index to next ques
                iQuestionIndex = iQuestionIndex + 1;

                if (iQuestionIndex < gd.model.size()) {
                    populateQuestion(iQuestionIndex);

                    iSubmitButton.setEnabled(true);
                    // iNextButton.setVisibility(View.GONE);
                } else {
                    // if the quiz is over
                    reInitialize();
                    getActivity().getFragmentManager().beginTransaction().replace(R.id.container, new ScoreFragment(), ScoreFragment.TAG).addToBackStack(null).commit();
                }
            }
        });
        // iNextButton.setVisibility(View.GONE);

        populateQuestion(iQuestionIndex);

        return view;
    }


    public void radioClick(View v) {

    }

    public void populateQuestion(int index) {
        for (int i = 0; i < iRadButtonList.size(); i++) {
            iRadButtonList.get(i).setBackgroundColor(Color.TRANSPARENT);
            iRadButtonList.get(i).setChecked(false);
            iRadButtonList.get(i).setVisibility(View.INVISIBLE);
        }

        iQuestion_no_Label.setText("Question #" + String.valueOf(index + 1)
                + " of " + gd.total);
        iQuestionLabel.setText(gd.model.get(index).getQuestion());
        ArrayList<String> options = gd.model.get(index).getOptions();
        for (int i = 0; i < options.size(); i++) {
            iRadButtonList.get(i).setText(options.get(i));
            iRadButtonList.get(i).setVisibility(View.VISIBLE);
        }
        iCurrentCorrectAnswer = Integer
                .parseInt(gd.model.get(index).getAnswer());

    }

    public int getSelectedAnswer() {
        int selected = -1;
        for (int i = 0; i < iRadButtonList.size(); i++) {
            if (iRadButtonList.get(i).isChecked()) {
                return i;
            }
        }
        return selected;
    }

    public void reInitialize() {

        iQuestionIndex = 0;
        gd.iQuizList.clear();
    }

}