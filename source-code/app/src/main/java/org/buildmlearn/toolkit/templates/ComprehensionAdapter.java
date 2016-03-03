package org.buildmlearn.toolkit.templates;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.activity.TemplateEditor;
import org.buildmlearn.toolkit.views.TextViewPlus;

import java.util.ArrayList;

/**
 * @brief Adapter for displaying Comprehension Template Editor data.
 * <p/>
 * Created by shikher on 02/03/16.
 */
public class ComprehensionAdapter extends BaseAdapter {

    private Context context;
    public ArrayList<ComprehensionModel> dataList;
    private int expandedPostion = -1;

    public ComprehensionAdapter(Context context, ArrayList<ComprehensionModel> data) {
        this.context = context;
        this.dataList = data;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public ComprehensionModel getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ComprehensionModel data = getItem(position);
        LayoutInflater mInflater;
        mInflater = LayoutInflater.from(context);
        QuizHolder quizHolder;
        ComprehensionHolder comprehensionHolder;
        if(data.isComprehension()) {
            if (convertView == null || (convertView.getTag()) instanceof QuizHolder) {
                convertView = mInflater.inflate(R.layout.comprehension_item, parent, false);
                comprehensionHolder = new ComprehensionHolder();
                comprehensionHolder.comprehensionIcon = (ImageView) convertView.findViewById(R.id.comprehension_icon);
                comprehensionHolder.comprehensionTitle = (TextViewPlus) convertView.findViewById(R.id.comprehension_title);
                comprehensionHolder.comprehensionContent = (TextViewPlus) convertView.findViewById(R.id.comprehension_content);
                comprehensionHolder.comprehensionContentBox = (LinearLayout) convertView.findViewById(R.id.comprehension_content_box);
                comprehensionHolder.clear = (Button) convertView.findViewById(R.id.comprehension_clean);
                comprehensionHolder.edit = (Button) convertView.findViewById(R.id.comprehension_edit);
            } else {
                comprehensionHolder = (ComprehensionHolder) convertView.getTag();
            }
            comprehensionHolder.comprehensionTitle.setText(data.getTitle());
            comprehensionHolder.comprehensionContent.setText(data.getComprehension());
            if (data.getQuizModel().isSelected()) {
                comprehensionHolder.comprehensionIcon.setImageResource(R.drawable.collapse);
                comprehensionHolder.comprehensionContentBox.setVisibility(View.VISIBLE);
            } else {
                comprehensionHolder.comprehensionIcon.setImageResource(R.drawable.expand);
                comprehensionHolder.comprehensionContentBox.setVisibility(View.GONE);
            }

            comprehensionHolder.comprehensionIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (expandedPostion >= 0 && expandedPostion != position && getItem(expandedPostion) != null) {
                        getItem(expandedPostion).getQuizModel().setIsSelected(false);
                    }
                    if (getItem(position).getQuizModel().isSelected()) {
                        getItem(position).getQuizModel().setIsSelected(false);
                        expandedPostion = -1;
                    } else {
                        getItem(position).getQuizModel().setIsSelected(true);
                        expandedPostion = position;
                    }
                    notifyDataSetChanged();
                }
            });

            comprehensionHolder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editItem(position, context);
                }
            });
            comprehensionHolder.clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final MaterialDialog dialog = new MaterialDialog.Builder(context)
                      .title(R.string.dialog_clear_title)
                      .content(R.string.dialog_clear_msg)
                      .positiveText(R.string.dialog_yes)
                      .negativeText(R.string.dialog_no)
                      .build();

                    dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ComprehensionModel model = dataList.get(position);
                            model.setComprehension(null);
                            model.setTitle(null);
                            notifyDataSetChanged();
                            dialog.dismiss();
                            ((TemplateEditor) context).restoreSelectedView();
                            expandedPostion = -1;
                        }
                    });
                    dialog.show();
                }
            });
            convertView.setTag(comprehensionHolder);
        } else {
            if(convertView == null || (convertView.getTag()) instanceof ComprehensionHolder) {
                convertView = mInflater.inflate(R.layout.quiz_item, parent, false);
                quizHolder = new QuizHolder();
                quizHolder.question = (TextViewPlus) convertView.findViewById(R.id.question);
                quizHolder.options = new ArrayList<>();
                quizHolder.options.add((TextViewPlus) convertView.findViewById(R.id.answer1));
                quizHolder.options.add((TextViewPlus) convertView.findViewById(R.id.answer2));
                quizHolder.options.add((TextViewPlus) convertView.findViewById(R.id.answer3));
                quizHolder.options.add((TextViewPlus) convertView.findViewById(R.id.answer4));
                quizHolder.questionIcon = (ImageView) convertView.findViewById(R.id.question_icon);
                quizHolder.quizOptionsBox = (LinearLayout) convertView.findViewById(R.id.quiz_options_box);
                quizHolder.delete = (Button) convertView.findViewById(R.id.quiz_item_delete);
                quizHolder.edit = (Button) convertView.findViewById(R.id.quiz_item_edit);
            } else {
                quizHolder = (QuizHolder) convertView.getTag();
            }
            quizHolder.question.setText(data.getQuizModel().getQuestion());
            if (data.getQuizModel().isSelected()) {
                quizHolder.questionIcon.setImageResource(R.drawable.collapse);
                quizHolder.quizOptionsBox.setVisibility(View.VISIBLE);
            } else {
                quizHolder.questionIcon.setImageResource(R.drawable.expand);
                quizHolder.quizOptionsBox.setVisibility(View.GONE);
            }

            for (int i = 0; i < quizHolder.options.size(); i++) {
                if (i < data.getQuizModel().getOptions().size()) {
                    int ascii = 65 + i;
                    quizHolder.options.get(i).setText(Character.toString((char) ascii) + ")  " + data.getQuizModel().getOptions().get(i));
                    quizHolder.options.get(i).setVisibility(View.VISIBLE);
                } else {
                    quizHolder.options.get(i).setVisibility(View.GONE);
                }
            }

            quizHolder.options.get(data.getQuizModel().getCorrectAnswer()).setCustomFont(context, "roboto_medium.ttf");
            quizHolder.options.get(data.getQuizModel().getCorrectAnswer()).setTextColor(context.getResources().getColor(R.color.quiz_correct_answer));

            quizHolder.questionIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (expandedPostion >= 0 && expandedPostion != position && getItem(expandedPostion) != null) {
                        getItem(expandedPostion).getQuizModel().setIsSelected(false);
                    }
                    if (getItem(position).getQuizModel().isSelected()) {
                        getItem(position).getQuizModel().setIsSelected(false);
                        expandedPostion = -1;
                    } else {
                        getItem(position).getQuizModel().setIsSelected(true);
                        expandedPostion = position;
                    }
                    notifyDataSetChanged();
                }
            });

            quizHolder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editItem(position, context);
                }
            });
            quizHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final MaterialDialog dialog = new MaterialDialog.Builder(context)
                      .title(R.string.dialog_delete_title)
                      .content(R.string.dialog_delete_msg)
                      .positiveText(R.string.dialog_yes)
                      .negativeText(R.string.dialog_no)
                      .build();

                    dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dataList.remove(position);
                            notifyDataSetChanged();
                            dialog.dismiss();

                            ((TemplateEditor) context).restoreSelectedView();
                            expandedPostion = -1;
                        }
                    });

                    dialog.show();
                }
            });
            convertView.setTag(quizHolder);
        }
        return convertView;
    }

    //different editing for comprehension and quiz question
    public void editItem(final int position, final Context context) {
        if(0==position)
            editComprehension(position, context);
        else
            editQuizItem(position, context);
    }

    public void editComprehension(final int position, final Context context) {
        ComprehensionModel data;
        // if dataList size is 0 that means we are adding a Comprehension not editing it.
        if(dataList.size()==0) {
            data = new ComprehensionModel(true, null, null, new QuizModel(null, null, 0));
        } else {
            data = getItem(position);
        }
        boolean wrapInScrollView = true;
        final MaterialDialog dialog = new MaterialDialog.Builder(context)
          .title(dataList.size()==0?R.string.comprehension_add: R.string.comprehension_edit)
          .customView(R.layout.comprehension_edit_dialog, wrapInScrollView)
          .positiveText(R.string.comprehension_ok)
          .negativeText(R.string.comprehension_cancel)
          .build();

        final EditText title= (EditText) dialog.findViewById(R.id.comprehension_title_edit_text);
        title.setText(data.getTitle());
        final EditText content = (EditText) dialog.findViewById(R.id.comprehension_content_edit_text);
        content.setText(data.getComprehension());
        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    String title_string = title.getText().toString();
                    String comprehension_string = content.getText().toString();
                    ComprehensionModel tempmodel = new ComprehensionModel(true, comprehension_string, title_string, new QuizModel(null, null, 0));
                    if(dataList.size()==0)
                        dataList.add(tempmodel);
                    else
                        dataList.set(position, tempmodel);
                    notifyDataSetChanged();
                    dialog.dismiss();
                }
        });
        dialog.show();
    }

    private void editQuizItem(final int position, final Context context) {
        ComprehensionModel data = getItem(position);

        boolean wrapInScrollView = true;
        final MaterialDialog dialog = new MaterialDialog.Builder(context)
          .title(R.string.quiz_edit)
          .customView(R.layout.quiz_dialog_add_question, wrapInScrollView)
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

        for (int i = 0; i < data.getQuizModel().getOptions().size(); i++) {
            options.get(i).setText(data.getQuizModel().getOptions().get(i));
        }

        question.setText(data.getQuizModel().getQuestion());
        buttons.get(data.getQuizModel().getCorrectAnswer()).setChecked(true);

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
                    ArrayList<String> answerOptions = new ArrayList<String>();
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
                    dataList.set(position, new ComprehensionModel(false, null, null, new QuizModel(questionText, answerOptions, correctAnswer)));
                    notifyDataSetChanged();
                }
            }
        });
        dialog.show();
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

    public class QuizHolder {
        TextViewPlus question;
        ImageView questionIcon;
        ArrayList<TextViewPlus> options;
        LinearLayout quizOptionsBox;
        Button delete;
        Button edit;
    }

    public class ComprehensionHolder {
        ImageView comprehensionIcon;
        TextViewPlus comprehensionTitle;
        LinearLayout comprehensionContentBox;
        TextViewPlus comprehensionContent;
        Button clear;
        Button edit;
    }
}
