package org.buildmlearn.toolkit.templates;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.comprehensiontemplate.TFTComprehensionFragment;
import org.buildmlearn.toolkit.model.TemplateInterface;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;

/**
 * @brief Comprehension template code implementing methods of TemplateInterface
 *
 * Created by shikher on 02/03/16 at 3:40 PM.
 */
public class ComprehensionTemplate implements TemplateInterface {

    transient private ComprehensionAdapter mAdapter;

    @Override
    public BaseAdapter newTemplateEditorAdapter(Context context) {
        mAdapter = new ComprehensionAdapter(context, new ArrayList<ComprehensionModel>());
        return mAdapter;
    }

    @Override
    public BaseAdapter currentTemplateEditorAdapter() {
        return mAdapter;
    }

    @Override
    public BaseAdapter loadProjectTemplateEditor(Context context, ArrayList<Element> data) {
        ArrayList<ComprehensionModel> quizData = new ArrayList<>();
        for (Element item : data) {
            quizData.add(ComprehensionModel.getModelFromElement(item));
        }
        mAdapter = new ComprehensionAdapter(context, quizData);
        return mAdapter;
    }

    @Override
    public String onAttach() {
        return "Comprehension Template";
    }

    @Override
    public String getTitle() {
        return "Comprehension Template";
    }

    //check if element is the first item as first item should be comprehension
    @Override
    public void addItem(final Activity activity) {
        if(mAdapter.getCount()==0) {
            //create a dialog for Comprehension
            addComprehension(activity);
        } else {
            //create a dialog for Quiz Question
            addQuizItem(activity);
        }
    }

    public void addComprehension(final Activity activity) {
        mAdapter.editComprehension(0, activity);
    }

    public void addQuizItem(final Activity activity) {
        final MaterialDialog dialog = new MaterialDialog.Builder(activity)
          .title(R.string.quiz_new_question_title)
          .customView(R.layout.quiz_dialog_add_question, true)
          .positiveText(R.string.quiz_add)
          .negativeText(R.string.quiz_cancel)
          .build();

        final EditText question = (EditText) dialog.findViewById(R.id.quiz_question);
        final ArrayList<RadioButton> buttons = new ArrayList<>();
        final ArrayList<EditText> options = new ArrayList<>();
        options.add((EditText) dialog.findViewById(R.id.quiz_option_1));
        options.add((EditText) dialog.findViewById(R.id.quiz_option_2));
        options.add((EditText) dialog.findViewById(R.id.quiz_option_3));
        options.add((EditText) dialog.findViewById(R.id.quiz_option_4));
        buttons.add((RadioButton) dialog.findViewById(R.id.quiz_radio_1));
        buttons.add((RadioButton) dialog.findViewById(R.id.quiz_radio_2));
        buttons.add((RadioButton) dialog.findViewById(R.id.quiz_radio_3));
        buttons.add((RadioButton) dialog.findViewById(R.id.quiz_radio_4));

        for (final RadioButton button : buttons) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkButton(buttons, options, button.getId(), activity);
                }
            });
        }

        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isValidated = true;
                int checkedAns = getCheckedAnswer(buttons);
                if (checkedAns < 0) {
                    Toast.makeText(activity, "Choose a correct option", Toast.LENGTH_SHORT).show();
                    isValidated = false;
                }
                if (question.getText().toString().equals("")) {

                    question.setError("Question is required");
                    isValidated = false;
                }

                int optionCount = 0;
                for (EditText option : options) {
                    if (!option.getText().toString().equals("")) {
                        optionCount++;
                    }
                }
                if (optionCount < 2) {
                    Toast.makeText(activity, "Minimum two multiple answers are required.", Toast.LENGTH_SHORT).show();
                    isValidated = false;
                }

                if (isValidated) {
                    dialog.dismiss();
                    ArrayList<String> answerOptions = new ArrayList<>();
                    int correctAnswer = 0;
                    for (int i = 0; i < buttons.size(); i++) {
                        if (buttons.get(i).isChecked() && !options.get(i).getText().toString().equals("")) {
                            correctAnswer = answerOptions.size();
                            answerOptions.add(options.get(i).getText().toString());
                        } else if (!options.get(i).getText().toString().equals("")) {
                            answerOptions.add(options.get(i).getText().toString());
                        }
                    }
                    String questionText = question.getText().toString();
                    mAdapter.dataList.add(ComprehensionModel.getComprehensionModelForQuizModel(new QuizModel(questionText, answerOptions, correctAnswer)));
                    mAdapter.notifyDataSetChanged();
                }

            }
        });
        dialog.show();
    }

    //check if trying to edit comprehension
    @Override
    public void editItem(final Activity activity, final int position) {
        if(0==position)
            editComprehenion(activity, position);
        else
            editQuizItem(activity, position);
    }

    public void editComprehenion(final Activity activity, final int position) {
        mAdapter.editItem(position, activity);
    }

    public void editQuizItem(final Activity activity, final int position) {
        ComprehensionModel data = mAdapter.dataList.get(position);

        final MaterialDialog dialog = new MaterialDialog.Builder(activity)
          .title(R.string.quiz_edit)
          .customView(R.layout.quiz_dialog_add_question, true)
          .positiveText(R.string.quiz_ok)
          .negativeText(R.string.quiz_cancel)
          .build();

        final EditText question = (EditText) dialog.findViewById(R.id.quiz_question);
        final ArrayList<RadioButton> buttons = new ArrayList<>();
        final ArrayList<EditText> options = new ArrayList<>();
        options.add((EditText) dialog.findViewById(R.id.quiz_option_1));
        options.add((EditText) dialog.findViewById(R.id.quiz_option_2));
        options.add((EditText) dialog.findViewById(R.id.quiz_option_3));
        options.add((EditText) dialog.findViewById(R.id.quiz_option_4));
        buttons.add((RadioButton) dialog.findViewById(R.id.quiz_radio_1));
        buttons.add((RadioButton) dialog.findViewById(R.id.quiz_radio_2));
        buttons.add((RadioButton) dialog.findViewById(R.id.quiz_radio_3));
        buttons.add((RadioButton) dialog.findViewById(R.id.quiz_radio_4));

        for (int i = 0; i < data.getQuizModel().getOptions().size(); i++) {
            options.get(i).setText(data.getQuizModel().getOptions().get(i));
        }

        question.setText(data.getQuizModel().getQuestion());
        buttons.get(data.getQuizModel().getCorrectAnswer()).setChecked(true);

        for (final RadioButton button : buttons) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkButton(buttons, options, button.getId(), activity);
                }
            });
        }

        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isValidated = true;
                int checkedAns = getCheckedAnswer(buttons);
                if (checkedAns < 0) {
                    Toast.makeText(activity, "Choose a correct option", Toast.LENGTH_SHORT).show();
                    isValidated = false;
                }
                if (question.getText().toString().equals("")) {

                    question.setError("Question is required");
                    isValidated = false;
                }

                int optionCount = 0;
                for (EditText option : options) {
                    if (!option.getText().toString().equals("")) {
                        optionCount++;
                    }
                }
                if (optionCount < 2) {
                    Toast.makeText(activity, "Minimum two multiple answers are required.", Toast.LENGTH_SHORT).show();
                    isValidated = false;
                }

                if (isValidated) {
                    dialog.dismiss();
                    ArrayList<String> answerOptions = new ArrayList<>();
                    int correctAnswer = 0;
                    for (int i = 0; i < buttons.size(); i++) {
                        if (buttons.get(i).isChecked() && !options.get(i).getText().toString().equals("")) {
                            correctAnswer = answerOptions.size();
                            answerOptions.add(options.get(i).getText().toString());
                        } else if (!options.get(i).getText().toString().equals("")) {
                            answerOptions.add(options.get(i).getText().toString());
                        }
                    }
                    String questionText = question.getText().toString();
                    mAdapter.dataList.set(position,ComprehensionModel.getComprehensionModelForQuizModel(new QuizModel(questionText, answerOptions, correctAnswer)));
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
        dialog.show();
    }

    //check if trying to delete comprehension. Can't delete comprehension, just clean it.
    @Override
    public void deleteItem(int position) {
        if(0==position) {
            Log.wtf("ComprehensionTemplate", "Can't remove Comprehension");
        }
        else {
            mAdapter.dataList.remove(position);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public ArrayList<Element> getItems(Document doc) {
        ArrayList<Element> itemElements = new ArrayList<>();
        for (ComprehensionModel data : mAdapter.dataList) {
            itemElements.add(data.getXml(doc));
        }
        return itemElements;
    }

    @Override
    public Fragment getSimulatorFragment(String filePathWithName) {
        return TFTComprehensionFragment.newInstance(filePathWithName);
    }

    @Override
    public String getAssetsFileName() {
        return "comprehension_content.xml";
    }

    @Override
    public String getAssetsFilePath() {
        return "assets/";
    }

    @Override
    public String getApkFilePath() {
        return "ComprehensionTemplateApp.apk";
    }

    @Override
    public void onActivityResult(Context context, int requestCode, int resultCode, Intent intent) {

    }

    private void checkButton(ArrayList<RadioButton> buttons, ArrayList<EditText> options, int id, Context context) {
        for (RadioButton button : buttons) {
            if (button.getId() == id) {
                int index = buttons.indexOf(button);
                if (options.get(index).getText().toString().equals("")) {
                    Toast.makeText(context, "Enter a valid option before marking it as answer", Toast.LENGTH_LONG).show();
                    button.setChecked(false);
                    return;
                } else {
                    button.setChecked(true);
                }
            } else {
                button.setChecked(false);
            }
        }
    }

    private int getCheckedAnswer(ArrayList<RadioButton> buttons) {
        for (int i = 0; i < buttons.size(); i++) {
            if (buttons.get(i).isChecked()) {
                return i;
            }
        }
        return -1;
    }

}
