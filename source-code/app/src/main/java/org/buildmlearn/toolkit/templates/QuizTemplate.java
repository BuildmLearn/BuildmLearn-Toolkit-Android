package org.buildmlearn.toolkit.templates;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.activity.TemplateEditorInterface;
import org.buildmlearn.toolkit.model.Template;
import org.buildmlearn.toolkit.model.TemplateInterface;
import org.buildmlearn.toolkit.quiztemplate.fragment.SplashFragment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @brief Quiz template code implementing methods of TemplateInterface
 * <p/>
 * Created by abhishek on 27/5/15.
 */
public class QuizTemplate implements TemplateInterface {


    transient private QuizAdapter mAdapter;
    private ArrayList<QuizModel> quizData;
    private int templateId;

    public QuizTemplate() {
        this.quizData = new ArrayList<>();
    }


    @Override
    public QuizAdapter newTemplateEditorAdapter(Context context, final TemplateEditorInterface templateEditorInterface) {
        mAdapter = new QuizAdapter(context, quizData) {
            @Override
            public boolean onLongItemClick(int position, View view) {
                return templateEditorInterface.onItemLongClick(position, view);
            }

            @Override
            protected String getAuthorName() {
                return templateEditorInterface.getAuthorName();
            }

            @Override
            protected void setAuthorName(String authorName) {
                templateEditorInterface.setAuthorName(authorName);
            }

            @Override
            protected void setTitle(String title) {
                templateEditorInterface.setProjectTitle(title);
            }

            @Override
            protected void restoreToolbarColorSchema() {
                templateEditorInterface.restoreColorSchema();
            }

            @Override
            protected String getTitle() {
                return templateEditorInterface.getProjectTitle();
            }
        };
        setEmptyView((Activity) context);
        return mAdapter;
    }

    @Override
    public BaseAdapter newMetaEditorAdapter(Context context) {
        return null;
    }

    @Override
    public Object currentTemplateEditorAdapter() {
        return mAdapter;
    }

    @Override
    public BaseAdapter currentMetaEditorAdapter() {
        return null;
    }

    @Override
    public BaseAdapter loadProjectMetaEditor(Context context, Document doc) {
        return null;
    }

    @Override
    public Object loadProjectTemplateEditor(Context context, ArrayList<Element> data, final TemplateEditorInterface templateEditorInterface) {
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
        mAdapter = new QuizAdapter(context, quizData) {
            @Override
            public boolean onLongItemClick(int position, View view) {
                return templateEditorInterface.onItemLongClick(position, view);
            }

            @Override
            protected String getAuthorName() {
                return templateEditorInterface.getAuthorName();
            }

            @Override
            protected void setAuthorName(String authorName) {
                templateEditorInterface.setAuthorName(authorName);
            }

            @Override
            protected void setTitle(String title) {
                templateEditorInterface.setProjectTitle(title);
            }

            @Override
            protected void restoreToolbarColorSchema() {
                templateEditorInterface.restoreColorSchema();
            }

            @Override
            protected String getTitle() {
                return templateEditorInterface.getProjectTitle();
            }
        };
        setEmptyView((Activity) context);
        return mAdapter;
    }

    @Override
    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    @Override
    public String getTitle() {
        return "Quiz Template";
    }

    @Override
    public void addItem(final Activity activity) {

        final View dialogView = View.inflate(activity,R.layout.quiz_dialog_add_question, null);
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.quiz_new_question_title)
                .setView(dialogView,
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_left),
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_top),
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_right),
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_bottom))
                .setPositiveButton(R.string.quiz_add, null)
                .setNegativeButton(R.string.quiz_cancel, null)
                .create();
        dialog.show();

        final EditText question = (EditText) dialogView.findViewById(R.id.quiz_question);
        final ArrayList<RadioButton> buttons = new ArrayList<>();
        final ArrayList<EditText> options = new ArrayList<>();
        options.add((EditText) dialogView.findViewById(R.id.quiz_option_1));
        options.add((EditText) dialogView.findViewById(R.id.quiz_option_2));
        options.add((EditText) dialogView.findViewById(R.id.quiz_option_3));
        options.add((EditText) dialogView.findViewById(R.id.quiz_option_4));
        buttons.add((RadioButton) dialogView.findViewById(R.id.quiz_radio_1));
        buttons.add((RadioButton) dialogView.findViewById(R.id.quiz_radio_2));
        buttons.add((RadioButton) dialogView.findViewById(R.id.quiz_radio_3));
        buttons.add((RadioButton) dialogView.findViewById(R.id.quiz_radio_4));

        for (final RadioButton button : buttons) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkButton(buttons, options, button.getId(), activity);
                }
            });
        }

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isValidated = true;

                if ("".equals(question.getText().toString().trim())) {
                    question.setError(activity.getString(R.string.enter_question));
                    isValidated = false;
                    return;
                }

                if (options.get(0).getText().toString().trim().equals("")) {
                    options.get(0).setError(activity.getString(R.string.cannot_be_empty));
                    isValidated = false;
                    return;
                }
                if (options.get(1).getText().toString().trim().equals("")) {
                    options.get(1).setError(activity.getString(R.string.cannot_be_empty));
                    isValidated = false;
                    return;
                }
                if (options.get(2).getText().toString().trim().equals("") && !options.get(3).getText().toString().trim().equals("")) {
                    options.get(2).hasFocus();
                    options.get(2).setError(activity.getString(R.string.comprehension_select_option_3_first));
                    isValidated = false;
                    return;
                }
                for(int i=0;i<options.size();i++){
                    for(int j=0;j<i;j++){
                        if (isValidated && !options.get(i).getText().toString().trim().isEmpty() && options.get(i).getText().toString().trim().equalsIgnoreCase(options.get(j).getText().toString().trim())) {
                            Toast.makeText(activity.getApplication(), activity.getString(R.string.same_options), Toast.LENGTH_SHORT).show();
                            isValidated = false;
                        }
                    }
                }


                int correctAnswer = 0;
                int checkedAns = getCheckedAnswer(buttons);

                if (checkedAns < 0) {
                    Toast.makeText(activity, activity.getString(R.string.comprehension_template_choose_correct_option), Toast.LENGTH_SHORT).show();
                    isValidated = false;
                    return;
                }
                for (EditText option : options) {
                    if ("".equals(option.getText().toString().trim())) {
                        option.setText("");
                        continue;
                    }
                    if (option.getText().toString() != null && "".equals(option.getText().toString().trim())) {
                        option.getText().clear();
                        option.setError(activity.getString(R.string.comprehension_template_valid_option));
                        isValidated = false;
                        return;
                    }
                }

                if (isValidated) {
                    dialog.dismiss();
                    ArrayList<String> answerOptions = new ArrayList<>();
                    correctAnswer = 0;
                    for (int i = 0; i < buttons.size(); i++) {
                        if (buttons.get(i).isChecked() && !"".equals(options.get(i).getText().toString().trim())) {
                            correctAnswer = answerOptions.size();
                            answerOptions.add(options.get(i).getText().toString().trim());
                        } else if (!"".equals(options.get(i).getText().toString().trim())) {
                            answerOptions.add(options.get(i).getText().toString().trim());
                        }
                    }
                    String questionText = question.getText().toString().trim();
                    quizData.add(new QuizModel(questionText, answerOptions, correctAnswer));
                    setEmptyView(activity);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public void addMetaData(Activity activity) {
        // This is intentionally empty
    }

    @Override
    public void editItem(final Activity activity, final int position) {
        QuizModel data = quizData.get(position);


        final View dialogView = View.inflate(activity,R.layout.quiz_dialog_add_question, null);
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.quiz_edit)
                .setView(dialogView,
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_left),
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_top),
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_right),
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_bottom))
                .setPositiveButton(R.string.quiz_ok, null)
                .setNegativeButton(R.string.quiz_cancel, null)
                .create();
        dialog.show();

        final EditText question = (EditText) dialogView.findViewById(R.id.quiz_question);
        final ArrayList<RadioButton> buttons = new ArrayList<>();
        final ArrayList<EditText> options = new ArrayList<>();
        options.add((EditText) dialogView.findViewById(R.id.quiz_option_1));
        options.add((EditText) dialogView.findViewById(R.id.quiz_option_2));
        options.add((EditText) dialogView.findViewById(R.id.quiz_option_3));
        options.add((EditText) dialogView.findViewById(R.id.quiz_option_4));
        buttons.add((RadioButton) dialogView.findViewById(R.id.quiz_radio_1));
        buttons.add((RadioButton) dialogView.findViewById(R.id.quiz_radio_2));
        buttons.add((RadioButton) dialogView.findViewById(R.id.quiz_radio_3));
        buttons.add((RadioButton) dialogView.findViewById(R.id.quiz_radio_4));

        for (int i = 0; i < data.getOptions().size(); i++) {
            options.get(i).setText(data.getOptions().get(i).trim());
        }

        question.setText(data.getQuestion());
        buttons.get(data.getCorrectAnswer()).setChecked(true);

        for (final RadioButton button : buttons) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkButton(buttons, options, button.getId(), activity);
                }
            });
        }

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean isValidated = true;

                if ("".equals(question.getText().toString().trim())) {
                    question.setError(activity.getString(R.string.enter_question));
                    isValidated = false;
                }

                if (options.get(0).getText().toString().trim().equals("")) {
                    options.get(0).setError(activity.getString(R.string.cannot_be_empty));
                    isValidated = false;
                    return;
                }
                if (options.get(1).getText().toString().trim().equals("")) {
                    options.get(1).setError(activity.getString(R.string.cannot_be_empty));
                    isValidated = false;
                    return;
                }
                if (options.get(2).getText().toString().trim().equals("") && !options.get(3).getText().toString().trim().equals("")) {
                    options.get(2).hasFocus();
                    options.get(2).setError(activity.getString(R.string.comprehension_select_option_3_first));
                    isValidated = false;
                    return;
                }

                int correctAnswer = 0;
                int checkedAns = getCheckedAnswer(buttons);

                if (checkedAns < 0) {
                    Toast.makeText(activity, activity.getString(R.string.comprehension_template_choose_correct_option), Toast.LENGTH_SHORT).show();
                    isValidated = false;
                    return;
                }

                for (EditText option : options) {
                    if ("".equals(option.getText().toString().trim())) {
                        option.setText("");
                        continue;
                    }
                    if (option.getText().toString() != null && "".equals(option.getText().toString().trim())) {
                        option.getText().clear();
                        option.setError(activity.getString(R.string.comprehension_template_valid_option));
                        isValidated = false;
                        return;
                    }
                }

                if (isValidated) {
                    dialog.dismiss();
                    ArrayList<String> answerOptions = new ArrayList<>();
                    correctAnswer = 0;
                    for (int i = 0; i < buttons.size(); i++) {
                        if (buttons.get(i).isChecked() && !"".equals(options.get(i).getText().toString().trim())) {
                            correctAnswer = answerOptions.size();
                            answerOptions.add(options.get(i).getText().toString().trim());
                        } else if (!"".equals(options.get(i).getText().toString().trim())) {
                            answerOptions.add(options.get(i).getText().toString().trim());
                        }
                    }
                    String questionText = question.getText().toString().trim();
                    quizData.set(position, new QuizModel(questionText, answerOptions, correctAnswer));
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public Object deleteItem(Activity activity, int position) {
        QuizModel quizModel = quizData.get(position);
        quizData.remove(position);
        setEmptyView(activity);
        mAdapter.notifyDataSetChanged();
        setEmptyView(activity);
        return quizModel;
    }

    @Override
    public void restoreItem(Activity activity, int position, Object object) {
        if (object instanceof QuizModel) {
            QuizModel quizModel = (QuizModel) object;
            if (quizModel != null) {
                quizData.add(position, quizModel);
                mAdapter.notifyDataSetChanged();
                setEmptyView(activity);
            }
        }
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
    public android.support.v4.app.Fragment getSimulatorFragment(String filePathWithName) {
        return SplashFragment.newInstance(filePathWithName);
    }

    @Override
    public String getAssetsFileName(Context context) {
        Template[] templates = Template.values();
        return context.getString(templates[templateId].getAssetsName());
    }

    @Override
    public String getAssetsFilePath() {
        return "assets/";
    }

    @Override
    public String getApkFilePath() {
        return "QuizTemplateApp.apk";
    }

    @Override
    public void onActivityResult(Context context, int requestCode, int resultCode, Intent intent) {
        // This is intentionally empty
    }

    @Override
    public boolean moveDown(Activity activity, int selectedPosition) {
        try {
            //Check already at last
            if (selectedPosition == quizData.size() - 1)
                return false;
            Collections.swap(quizData, selectedPosition, selectedPosition + 1);
            mAdapter.notifyDataSetChanged();
            return true;
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean moveUp(Activity activity, int selectedPosition) {
        try {
            //Check already at top
            if (selectedPosition == 0)
                return false;
            Collections.swap(quizData, selectedPosition, selectedPosition - 1);
            mAdapter.notifyDataSetChanged();
            return true;
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return false;
    }


    private void checkButton(ArrayList<RadioButton> buttons, ArrayList<EditText> options, int id, Context context) {
        for (RadioButton button : buttons) {
            if (button.getId() == id) {
                int index = buttons.indexOf(button);
                if ("".equals(options.get(index).getText().toString().trim())) {
                    options.get(index).setError(context.getString(R.string.enter_valid_option));
                    options.get(index).setText(null);
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

    /**
     * @brief Toggles the visibility of empty text if Array has zero elements
     */
    private void setEmptyView(Activity activity) {
        if (quizData.size() < 1) {
            activity.findViewById(R.id.empty).setVisibility(View.VISIBLE);
        } else {
            activity.findViewById(R.id.empty).setVisibility(View.GONE);
        }
    }
}