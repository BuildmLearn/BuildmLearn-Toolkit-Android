package org.buildmlearn.toolkit.flashcardtemplate;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;


/**
 * @brief Simulator code for Flash Card Template
 */
public class MainFragment extends Fragment implements
        AnimationListener {
    View answerView, questionView;
    Button flipButton;
    Button preButton;
    Button nextButton;
    boolean isFlipped = false;
    int iQuestionIndex = 0;
    GlobalData gd = GlobalData.getInstance();
    String flashCardanswer;
    ImageView questionImage;
    TextView flashcardNumber;
    TextView questionText, hintText;
    private Animation animation1;
    private Animation animation2;
    private View currentView;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.flash_simu_main, container, false);

        animation1 = AnimationUtils.loadAnimation(getActivity(), R.anim.to_middle);
        animation1.setAnimationListener(this);
        animation2 = AnimationUtils.loadAnimation(getActivity(), R.anim.from_middle);
        animation2.setAnimationListener(this);

        questionView = view.findViewById(R.id.questionInMain);
        answerView = view.findViewById(R.id.answerInMain);

        questionView.setVisibility(View.VISIBLE);
        answerView.setVisibility(View.GONE);
        iQuestionIndex = 0;

        questionImage = (ImageView) view.findViewById(R.id.questionImage);
        questionText = (TextView) view.findViewById(R.id.flashCardText);
        hintText = (TextView) view.findViewById(R.id.questionhint);
        flashcardNumber = (TextView) view.findViewById(R.id.flashCardNumber);

        flipButton = (Button) view.findViewById(R.id.flip_button);
        preButton = (Button) view.findViewById(R.id.pre_button);
        nextButton = (Button) view.findViewById(R.id.next_button);

        populateQuestion(iQuestionIndex);
        currentView = questionView;

        flipButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                currentView.clearAnimation();
                currentView.setAnimation(animation1);
                currentView.startAnimation(animation1);

            }
        });

        preButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (iQuestionIndex != 0) {
                    isFlipped = false;

                    iQuestionIndex--;
                    questionView.setVisibility(View.VISIBLE);
                    answerView.setVisibility(View.GONE);
                    currentView = questionView;

                    populateQuestion(iQuestionIndex);
                }

            }
        });

        nextButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (iQuestionIndex < gd.model.size() - 1) {
                    isFlipped = false;
                    iQuestionIndex++;
                    questionView.setVisibility(View.VISIBLE);
                    answerView.setVisibility(View.GONE);
                    currentView = questionView;
                    populateQuestion(iQuestionIndex);

                } else {

                    getActivity().getFragmentManager().beginTransaction().replace(R.id.container, new ScoreFragment()).addToBackStack(null).commit();

                    reInitialize();
                }

            }
        });
        return view;
    }


    public void populateQuestion(int index) {
        if (index == 0) {
            preButton.setEnabled(false);

        } else
            preButton.setEnabled(true);

        int cardNum = index + 1;
        flashcardNumber.setText("Card #" + cardNum + " of " + gd.totalCards);
        FlashModel mFlash = gd.model.get(index);
        TextView answerText = (TextView) view.findViewById(R.id.answerText);
        if (mFlash.getQuestion() != null)
            questionText.setText(mFlash.getQuestion());
        if (mFlash.getHint() != null)
            hintText.setText(mFlash.getHint());
        if (mFlash.getAnswer() != null) {
            flashCardanswer = mFlash.getAnswer();
            answerText.setText(mFlash.getAnswer());
        }
        if (mFlash.getBase64() != null) {
            byte[] decodedString = Base64.decode(mFlash.getBase64(),
                    Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString,
                    0, decodedString.length);
            questionImage.setImageBitmap(decodedByte);
        } else {
            questionText.setGravity(Gravity.CENTER_VERTICAL);
        }

    }

    public void reInitialize() {
        iQuestionIndex = 0;
        gd.model.clear();
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if (animation == animation1) {

            TextView answerText = (TextView) view.findViewById(R.id.answerText);

            if (!isFlipped) {

                answerView.setVisibility(View.VISIBLE);
                questionView.setVisibility(View.GONE);
                isFlipped = true;
                answerText.setText(flashCardanswer);
                currentView = answerView;
            } else {

                isFlipped = false;
                answerText.setText("");
                questionView.setVisibility(View.VISIBLE);
                answerView.setVisibility(View.GONE);
                currentView = questionView;
            }
            currentView.clearAnimation();
            currentView.setAnimation(animation2);
            currentView.startAnimation(animation2);

        }

    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAnimationStart(Animation animation) {
        // TODO Auto-generated method stub

    }


}
