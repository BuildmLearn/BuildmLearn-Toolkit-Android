package org.buildmlearn.toolkit.templates;

import android.app.Fragment;
import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.model.TemplateInterface;
import org.buildmlearn.toolkit.quiztemplate.TFTQuizFragment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * Created by abhishek on 27/5/15.
 */
public class QuizTemplate implements TemplateInterface {


    transient private QuizAdapter mAdapter;
    private ArrayList<QuizModel> quizData;

    public QuizTemplate() {
        this.quizData = new ArrayList<>();
    }

    @Override
    public BaseAdapter newTemplateEditorAdapter(Context context) {
        mAdapter = new QuizAdapter(context, quizData);
        return mAdapter;
    }

    @Override
    public BaseAdapter currentTemplateEditorAdapter() {
        return mAdapter;
    }

    @Override
    public BaseAdapter loadProjectTemplateEditor(Context context, ArrayList<Element> data) {
        quizData = new ArrayList<>();
        for (Element item : data) {
            String question = item.getElementsByTagName("question").item(0).getTextContent();
            NodeList options = item.getElementsByTagName("option");
            ArrayList<String> answers = new ArrayList<>();
            for (int i = 0; i < options.getLength(); i++) {
                answers.add(options.item(i).getTextContent());
            }
            int answer = Integer.parseInt(item.getElementsByTagName("answer").item(0).getTextContent());
            quizData.add(new QuizModel(question, answers, answer));

        }
        mAdapter = new QuizAdapter(context, quizData);
        return mAdapter;
    }

    @Override
    public String onAttach() {
        return "Quiz Template";
    }

    @Override
    public String getTitle() {
        return "Quiz Template";
    }

    @Override
    public void addItem(final Context context) {
        final MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title(R.string.quiz_new_question_title)
                .customView(R.layout.quiz_dialog_add_question, true)
                .positiveText(R.string.quiz_add)
                .negativeText(R.string.quiz_delete)
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
                    checkButton(buttons, options, button.getId(), context);
                }
            });
        }

        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isValidated = true;
                int checkedAns = getCheckedAnswer(buttons);
                if (checkedAns < 0) {
                    Toast.makeText(context, "Choose a correct option", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(context, "Minimum two multiple answers are required.", Toast.LENGTH_SHORT).show();
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
                    quizData.add(new QuizModel(questionText, answerOptions, correctAnswer));
                    mAdapter.notifyDataSetChanged();
                }

            }
        });
        dialog.show();

    }

    @Override
    public void editItem(final Context context, final int position) {
        QuizModel data = quizData.get(position);

        final MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title(R.string.quiz_edit)
                .customView(R.layout.quiz_dialog_add_question, true)
                .positiveText(R.string.quiz_add)
                .negativeText(R.string.quiz_delete)
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

        for (int i = 0; i < data.getOptions().size(); i++) {
            options.get(i).setText(data.getOptions().get(i));
        }

        question.setText(data.getQuestion());
        buttons.get(data.getCorrectAnswer()).setChecked(true);

        for (final RadioButton button : buttons) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkButton(buttons, options, button.getId(), context);
                }
            });
        }

        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isValidated = true;
                int checkedAns = getCheckedAnswer(buttons);
                if (checkedAns < 0) {
                    Toast.makeText(context, "Choose a correct option", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(context, "Minimum two multiple answers are required.", Toast.LENGTH_SHORT).show();
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
                    quizData.set(position, new QuizModel(questionText, answerOptions, correctAnswer));
                    mAdapter.notifyDataSetChanged();
                }

            }
        });
        dialog.show();
    }

    @Override
    public void deleteItem(int position) {
        quizData.remove(position);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public ArrayList<Element> getItems(Document doc) {

        ArrayList<Element> itemElements = new ArrayList<>();


        for (QuizModel data : quizData) {

            itemElements.add(data.getXml(doc));
        }

        return itemElements;

    }

    @Override
    public Fragment getSimulatorFragment(String filePathWithName) {
        return TFTQuizFragment.newInstance(filePathWithName);
    }

    @Override
    public String getAssetsFilePath() {
        return "assets/quiz_content.xml";
    }

    @Override
    public String getApkFilePath() {
        return "quiz_template.apk";
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
