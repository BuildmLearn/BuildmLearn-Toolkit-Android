package org.buildmlearn.toolkit.templates;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.activity.TemplateEditor;
import org.buildmlearn.toolkit.views.TextViewPlus;

import java.util.ArrayList;

/**
 * Created by Anupam (opticod) on 26/5/16.
 */

/**
 * @brief Adapter for displaying Comprehension Template Editor data.
 * <p/>
 */
class ComprehensionAdapter extends BaseAdapter {


    private final Context context;
    private final ArrayList<ComprehensionModel> comprehensionData;
    private int expandedPostion = -1;

    public ComprehensionAdapter(Context context, ArrayList<ComprehensionModel> comprehensionData) {
        this.context = context;
        this.comprehensionData = comprehensionData;
    }

    @Override
    public int getCount() {
        return comprehensionData.size();
    }

    @Override
    public ComprehensionModel getItem(int position) {
        return comprehensionData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater;
        mInflater = LayoutInflater.from(context);
        Holder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.quiz_item, parent, false);
            holder = new Holder();

            holder.question = (TextViewPlus) convertView.findViewById(R.id.question);
            holder.options = new ArrayList<>();
            holder.options.add((TextViewPlus) convertView.findViewById(R.id.answer1));
            holder.options.add((TextViewPlus) convertView.findViewById(R.id.answer2));
            holder.options.add((TextViewPlus) convertView.findViewById(R.id.answer3));
            holder.options.add((TextViewPlus) convertView.findViewById(R.id.answer4));
            holder.questionIcon = (ImageView) convertView.findViewById(R.id.question_icon);
            holder.quizOptionsBox = (LinearLayout) convertView.findViewById(R.id.quiz_options_box);
            holder.delete = (Button) convertView.findViewById(R.id.quiz_item_delete);
            holder.edit = (Button) convertView.findViewById(R.id.quiz_item_edit);

        } else {
            holder = (Holder) convertView.getTag();
        }

        ComprehensionModel data = getItem(position);
        holder.question.setText(data.getQuestion());
        if (data.isSelected()) {
            holder.questionIcon.setImageResource(R.drawable.collapse);
            holder.quizOptionsBox.setVisibility(View.VISIBLE);
        } else {
            holder.questionIcon.setImageResource(R.drawable.expand);
            holder.quizOptionsBox.setVisibility(View.GONE);
        }

        for (int i = 0; i < holder.options.size(); i++) {
            if (i < data.getOptions().size()) {
                int ascii = 65 + i;
                holder.options.get(i).setText(String.format("%s) %s", Character.toString((char) ascii), data.getOptions().get(i)));
                holder.options.get(i).setTextColor(ContextCompat.getColor(context, R.color.black_secondary_text));
                holder.options.get(i).setVisibility(View.VISIBLE);
            } else {
                holder.options.get(i).setVisibility(View.GONE);
            }
        }

        holder.options.get(data.getCorrectAnswer()).setCustomFont(context, "roboto_medium.ttf");
        holder.options.get(data.getCorrectAnswer()).setTextColor(ContextCompat.getColor(context, R.color.quiz_correct_answer));

        holder.questionIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandedPostion >= 0 && expandedPostion != position && getItem(expandedPostion) != null) {
                    getItem(expandedPostion).setIsSelected(false);
                }
                if (getItem(position).isSelected()) {
                    getItem(position).setIsSelected(false);
                    expandedPostion = -1;
                } else {
                    getItem(position).setIsSelected(true);
                    expandedPostion = position;
                }
                notifyDataSetChanged();
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editItem(position, context);
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ComprehensionModel comprehensionModel = comprehensionData.get(position);
                comprehensionData.remove(position);
                notifyDataSetChanged();
                Snackbar.make(v,R.string.snackbar_deleted_message,Snackbar.LENGTH_LONG)
                        .setAction(R.string.snackbar_undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        comprehensionData.add(position,comprehensionModel);
                        notifyDataSetChanged();
                        Snackbar.make(v,R.string.snackbar_restored_message,Snackbar.LENGTH_LONG).show();
                    }
                }).show();

                ((TemplateEditor) context).restoreSelectedView();
                expandedPostion = -1;
            }
        });
        convertView.setTag(holder);
        return convertView;
    }

    private void editItem(final int position, final Context context) {
        ComprehensionModel data = getItem(position);

        LayoutInflater inflater = LayoutInflater.from(context);
        final View dialogView = inflater.inflate(R.layout.quiz_dialog_add_question, null);
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.quiz_edit)
                .setView(dialogView,
                        context.getResources().getDimensionPixelSize(R.dimen.spacing_left),
                        context.getResources().getDimensionPixelSize(R.dimen.spacing_top),
                        context.getResources().getDimensionPixelSize(R.dimen.spacing_right),
                        context.getResources().getDimensionPixelSize(R.dimen.spacing_bottom))
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

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
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
                    comprehensionData.set(position, new ComprehensionModel(questionText, answerOptions, correctAnswer));
                    notifyDataSetChanged();
                }

            }
        });
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

    public class Holder {
        public TextViewPlus question;
        public ImageView questionIcon;
        public ArrayList<TextViewPlus> options;
        public LinearLayout quizOptionsBox;
        public Button delete;
        public Button edit;
    }
}
