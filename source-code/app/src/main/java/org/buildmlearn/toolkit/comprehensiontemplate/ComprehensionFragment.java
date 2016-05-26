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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @brief Simulator code for Comprehension Template
 */
public class ComprehensionFragment extends Fragment {

    public final static String TAG = "COMPREHENSION_FRAGMENT";

    private TextView timeLeft;
    private Timer timer = new Timer();
    private int timeInSecond;
    private LinearLayout pauseScreenContainer;
    private TextView pauseMessage;
    private Button resumeProceedButton;
    private ScrollView scrollView;
    private FragmentActivity faActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        GlobalData gd = GlobalData.getInstance();
        faActivity = (FragmentActivity) super.getActivity();
        timeInSecond = Integer.parseInt(gd.iTime)*60;
        View view = inflater.inflate(R.layout.comprehension_template_fragment_passage_view, container, false);
        timeLeft = (TextView) view.findViewById(R.id.time_left);
        scheduleTimer();
        TextView passageTitle = (TextView) view.findViewById(R.id.passage_title);
        passageTitle.setText(gd.iPassageTitle);
        TextView passageContent = (TextView) view.findViewById(R.id.passage_content);
        passageContent.setText(gd.iPassage);
        Button doneButton = (Button) view.findViewById(R.id.done_button);
        doneButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                getActivity().getFragmentManager().beginTransaction().replace(R.id.container, new QuestionFragment(), QuestionFragment.TAG).addToBackStack(null).commit();
            }
        });

        Button pauseButton = (Button) view.findViewById(R.id.pause_button);
        pauseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showPausedState(true);
            }
        });

        pauseScreenContainer = (LinearLayout) view.findViewById(R.id.pause_screen_container);
        resumeProceedButton = (Button) view.findViewById(R.id.resume_proceed_button);
        pauseMessage = (TextView) view.findViewById(R.id.pause_msg);
        scrollView = (ScrollView) view.findViewById(R.id.scroll_view);
        return view;
    }

    private void showPausedState(boolean isPaused) {
        if(isPaused) {
            timer.cancel();
            //HIDE the screen
            pauseScreenContainer.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.GONE);
            if(timeInSecond>0) {
                pauseMessage.setText("Timer paused");
                resumeProceedButton.setText("Resume");
                resumeProceedButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPausedState(false);
                    }
                });
            } else { //time is finished
                pauseMessage.setText("Time is up");
                resumeProceedButton.setText("Start Quiz");
                resumeProceedButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().getFragmentManager().beginTransaction().replace(R.id.container, new QuestionFragment(), QuestionFragment.TAG).addToBackStack(null).commit();
                    }
                });
            }
        } else {
            timer = new Timer();
            scheduleTimer();
            //SHOW the screen
            pauseScreenContainer.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);
        }
    }

    private void scheduleTimer() {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                faActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(timeInSecond>0) {
                            String displayTime = timeInSecond/60 + " minutes and " + timeInSecond%60 + " seconds left.";
                            timeLeft.setText(displayTime);
                            timeInSecond--;
                        } else {
                            showPausedState(true);
                        }
                    }
                });
            }
        },1000, 1000);// 1000 milliseconds
    }
}