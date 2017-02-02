package org.buildmlearn.toolkit.templates;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.activity.TemplateEditor;
import org.buildmlearn.toolkit.views.TextViewPlus;
import org.buildmlearn.toolkit.views.dragdroprecyclerview.ItemTouchHelperAdapter;
import org.buildmlearn.toolkit.views.dragdroprecyclerview.ItemTouchHelperViewHolder;

import java.util.ArrayList;

/**
 * @brief Adapter for displaying Quiz Template Editor data.
 * <p/>
 * Created by abhishek on 28/5/15.
 */
abstract class QuizAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements ItemTouchHelperAdapter {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private final Context context;
    private final ArrayList<QuizModel> quizData;
    private int expandedPostion = -1;

    protected abstract boolean onLongItemClick(int position, View view);
    protected abstract String getTitle();
    protected abstract void restoreToolbarColorSchema();
    protected abstract String getAuthorName();
    protected abstract void setAuthorName(String authorName);
    protected abstract void setTitle(String title);

    QuizAdapter(Context context, ArrayList<QuizModel> quizData) {
        this.context = context;
        this.quizData = quizData;
    }

    public QuizModel getItem(int position) {
        try {
            return quizData.get(position);
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_ITEM) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_item, parent, false);
            return new QuizAdapterHolder(view);
        } else if (viewType == TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_header_template, parent, false);
            return new HeaderHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof QuizAdapterHolder) {
            QuizAdapterHolder quizAdapterHolder = (QuizAdapterHolder) viewHolder;
            QuizModel data = getItem(viewHolder.getLayoutPosition() - 1);
            quizAdapterHolder.view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return onLongItemClick(viewHolder.getLayoutPosition(), v);
                }
            });
            quizAdapterHolder.question.setText(data.getQuestion());
            if (data.isSelected()) {
                quizAdapterHolder.questionIcon.setImageResource(R.drawable.collapse);
                quizAdapterHolder.quizOptionsBox.setVisibility(View.VISIBLE);
            } else {
                quizAdapterHolder.questionIcon.setImageResource(R.drawable.expand);
                quizAdapterHolder.quizOptionsBox.setVisibility(View.GONE);
            }

            for (int i = 0; i < quizAdapterHolder.options.size(); i++) {
                if (i < data.getOptions().size()) {
                    int ascii = 65 + i;
                    quizAdapterHolder.options.get(i).setText(String.format("%s) %s", Character.toString((char) ascii), data.getOptions().get(i)));
                    quizAdapterHolder.options.get(i).setTextColor(ContextCompat.getColor(context, R.color.black_secondary_text));
                    quizAdapterHolder.options.get(i).setVisibility(View.VISIBLE);
                } else {
                    quizAdapterHolder.options.get(i).setVisibility(View.GONE);
                }
            }

            quizAdapterHolder.options.get(data.getCorrectAnswer()).setCustomFont(context, "roboto_medium.ttf");
            quizAdapterHolder.options.get(data.getCorrectAnswer()).setTextColor(ContextCompat.getColor(context, R.color.quiz_correct_answer));

            quizAdapterHolder.questionIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (expandedPostion >= 0 && expandedPostion != viewHolder.getLayoutPosition() && getItem(expandedPostion - 1) != null) {
                        getItem(expandedPostion - 1).setIsSelected(false);
                    }
                    if (getItem(viewHolder.getLayoutPosition() - 1).isSelected()) {
                        getItem(viewHolder.getLayoutPosition() - 1).setIsSelected(false);
                        expandedPostion = -1;
                    } else {
                        getItem(viewHolder.getLayoutPosition() - 1).setIsSelected(true);
                        expandedPostion = viewHolder.getLayoutPosition() - 1;
                    }
                    notifyDataSetChanged();
                }
            });

            quizAdapterHolder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editItem(viewHolder.getLayoutPosition()-1, context);
                }
            });
            quizAdapterHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final QuizModel quizModel = quizData.get(viewHolder.getLayoutPosition()-1);
                    quizData.remove(viewHolder.getLayoutPosition()-1);
                    notifyDataSetChanged();
                    Snackbar.make(v, R.string.snackbar_deleted_message, Snackbar.LENGTH_LONG)
                            .setAction(R.string.snackbar_undo, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (viewHolder.getLayoutPosition() - 1 >= 0) {
                                        quizData.add(viewHolder.getLayoutPosition() - 1, quizModel);
                                    } else {
                                        quizData.add(quizModel);
                                    }
                                    notifyDataSetChanged();
                                    Snackbar.make(v, R.string.snackbar_restored_message, Snackbar.LENGTH_LONG).show();
                                }
                            }).show();

                    ((TemplateEditor) context).restoreSelectedView();
                    expandedPostion = -1;
                }
            });
        } else if (viewHolder instanceof HeaderHolder) {
            final HeaderHolder headerHolder = (HeaderHolder) viewHolder;
            try {
                headerHolder.authorEditText.setText(getAuthorName());
                headerHolder.titleEditText.setText(getTitle());
                handleTextChange(headerHolder.authorEditText, headerHolder.titleEditText);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleTextChange(final EditText authorEditText, final EditText titleEditText) {
        authorEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e(getClass().getName(), "beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setAuthorName(authorEditText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e(getClass().getName(), "afterTextChanged");
            }
        });
        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e(getClass().getName(), "beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setTitle(titleEditText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e(getClass().getName(), "afterTextChanged");
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return quizData.size() + 1;
    }

    private void editItem(final int position, final Context context) {
        QuizModel data = getItem(position);


        final View dialogView = View.inflate(context,R.layout.quiz_dialog_add_question, null);
        final AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.quiz_edit)
                .setView(dialogView,
                        context.getResources().getDimensionPixelSize(R.dimen.spacing_left),
                        context.getResources().getDimensionPixelSize(R.dimen.spacing_top),
                        context.getResources().getDimensionPixelSize(R.dimen.spacing_right),
                        context.getResources().getDimensionPixelSize(R.dimen.spacing_bottom))
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
                    Toast.makeText(context, R.string.choose_correct_option, Toast.LENGTH_SHORT).show();
                    isValidated = false;
                }
                if (question.getText().toString().equals("")) {

                    question.setError(context.getString(R.string.ques_required));
                    isValidated = false;
                }

                int optionCount = 0;
                for (EditText option : options) {
                    if (!option.getText().toString().equals("")) {
                        optionCount++;
                    }
                }
                if (optionCount < 2) {
                    Toast.makeText(context, R.string.min_answers_required, Toast.LENGTH_SHORT).show();
                    isValidated = false;
                }

                for (int i = 0; i < options.size(); i++) {
                    for (int j = 0; j < i; j++) {
                        if (!options.get(i).getText().toString().trim().isEmpty() && options.get(i).getText().toString().trim().equalsIgnoreCase(options.get(j).getText().toString().trim())) {
                            Toast.makeText(context, context.getString(R.string.same_options), Toast.LENGTH_SHORT).show();
                            isValidated = false;
                        }
                    }
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
                    Toast.makeText(context, R.string.enter_valid_option, Toast.LENGTH_LONG).show();
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

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (toPosition == 0)
            return false;
        try {
            QuizModel prev = quizData.remove(fromPosition - 1);
            quizData.add(toPosition > fromPosition ? toPosition - 1 : toPosition - 1, prev);
            notifyItemMoved(fromPosition, toPosition);
            restoreToolbarColorSchema();
            return true;
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static class HeaderHolder extends RecyclerView.ViewHolder {
        private EditText authorEditText, titleEditText;

        HeaderHolder(View itemView) {
            super(itemView);
            authorEditText = (EditText) itemView.findViewById(R.id.author_name);
            titleEditText = (EditText) itemView.findViewById(R.id.template_title);
        }
    }

    private static class QuizAdapterHolder extends RecyclerView.ViewHolder
            implements ItemTouchHelperViewHolder {
        private View view;
        private TextViewPlus question;
        private ImageView questionIcon;
        private ArrayList<TextViewPlus> options;
        private LinearLayout quizOptionsBox;
        private Button delete;
        private Button edit;

        private QuizAdapterHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            question = (TextViewPlus) itemView.findViewById(R.id.question);
            options = new ArrayList<>();
            options.add((TextViewPlus) itemView.findViewById(R.id.answer1));
            options.add((TextViewPlus) itemView.findViewById(R.id.answer2));
            options.add((TextViewPlus) itemView.findViewById(R.id.answer3));
            options.add((TextViewPlus) itemView.findViewById(R.id.answer4));
            questionIcon = (ImageView) itemView.findViewById(R.id.question_icon);
            quizOptionsBox = (LinearLayout) itemView.findViewById(R.id.quiz_options_box);
            delete = (Button) itemView.findViewById(R.id.quiz_item_delete);
            edit = (Button) itemView.findViewById(R.id.quiz_item_edit);
        }

        @Override
        public void onItemSelected() {
            Log.e(getClass().getName(), "Item Selected to drag");
        }
    }
}
