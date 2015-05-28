package org.buildmlearn.toolkit.templates;

import android.content.Context;
import android.widget.BaseAdapter;

import com.afollestad.materialdialogs.MaterialDialog;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.model.TemplateInterface;

import java.util.ArrayList;

/**
 * Created by abhishek on 27/5/15.
 */
public class QuizTemplate implements TemplateInterface {


    transient private QuizAdapter mAdapter;
    private ArrayList<QuizModel> quizData;
    private int count;

    public QuizTemplate() {
        count = 0;
        this.quizData = new ArrayList<>();
    }

    @Override
    public BaseAdapter newTemplateEditorAdapter(Context context) {
        ArrayList<String> options = new ArrayList<>();
        options.add("New Delhi");
        options.add("Banglore");
        options.add("Hyderabad");
        options.add("Mumbai");
        quizData.add(new QuizModel("What is the capital of India ?", options, 0));
        quizData.add(new QuizModel("Who is the prime minister of India ?", options, 0));
        quizData.add(new QuizModel("Whats is the atomic weight of Gold ?", options, 0));
        quizData.add(new QuizModel("What is the capital of India ?", options, 0));
        quizData.add(new QuizModel("Who is the prime minister of India ?", options, 0));
        quizData.add(new QuizModel("Whats is the atomic weight of Gold ?", options, 0));
        quizData.add(new QuizModel("What is the capital of India ?", options, 0));
        quizData.add(new QuizModel("Who is the prime minister of India ?", options, 0));
        quizData.add(new QuizModel("Whats is the atomic weight of Gold ?", options, 0));
        quizData.add(new QuizModel("What is the capital of India ?", options, 0));
        quizData.add(new QuizModel("Who is the prime minister of India ?", options, 0));
        quizData.add(new QuizModel("Whats is the atomic weight of Gold ?", options, 0));
        quizData.add(new QuizModel("What is the capital of India ?", options, 0));
        quizData.add(new QuizModel("Who is the prime minister of India ?", options, 0));
        quizData.add(new QuizModel("Whats is the atomic weight of Gold ?", options, 0));
        quizData.add(new QuizModel("What is the capital of India ?", options, 0));
        quizData.add(new QuizModel("Who is the prime minister of India ?", options, 0));
        quizData.add(new QuizModel("Whats is the atomic weight of Gold ?", options, 0));
        quizData.add(new QuizModel("What is the capital of India ?", options, 0));
        quizData.add(new QuizModel("Who is the prime minister of India ?", options, 0));
        quizData.add(new QuizModel("Whats is the atomic weight of Gold ?", options, 0));
        mAdapter = new QuizAdapter(context, quizData);
        return mAdapter;
    }

    @Override
    public BaseAdapter currentTemplateEditorAdapter() {
        return mAdapter;
    }

    @Override
    public String onAttach() {
        count++;
        return "This is Quiz Template, Count: " + count;
    }

    @Override
    public String getTitle() {
        return "Quiz Template";
    }

    @Override
    public void addItem(final Context context) {
        boolean wrapInScrollView = true;
        new MaterialDialog.Builder(context)
                .title(R.string.quiz_new_question_title)
                .customView(R.layout.quiz_dialog_add_question, wrapInScrollView)
                .positiveText(R.string.quiz_add)
                .negativeText(R.string.quiz_delete)
                .show();
    }

//    private void showCustomView(Context context) {
//        MaterialDialog dialog = new MaterialDialog.Builder(context)
//                .title(R.string.googleWifi)
//                .customView(R.layout.dialog_customview, true)
//                .positiveText(R.string.connect)
//                .negativeText(android.R.string.cancel)
//                .callback(new MaterialDialog.ButtonCallback() {
//                    @Override
//                    public void onPositive(MaterialDialog dialog) {
//                        showToast("Password: " + passwordInput.getText().toString());
//                    }
//
//                    @Override
//                    public void onNegative(MaterialDialog dialog) {
//                    }
//                }).build();
//
//        final View positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
//        //noinspection ConstantConditions
//        passwordInput = (EditText) dialog.getCustomView().findViewById(R.id.password);
//        passwordInput.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                positiveAction.setEnabled(s.toString().trim().length() > 0);
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });
//
//        // Toggling the show password CheckBox will mask or unmask the password input EditText
//        CheckBox checkbox = (CheckBox) dialog.getCustomView().findViewById(R.id.showPassword);
//        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                passwordInput.setInputType(!isChecked ? InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_CLASS_TEXT);
//                passwordInput.setTransformationMethod(!isChecked ? PasswordTransformationMethod.getInstance() : null);
//            }
//        });
//
//        int widgetColor = ThemeSingleton.get().widgetColor;
//        MDTintHelper.setTint(checkbox,
//                widgetColor == 0 ? getResources().getColor(R.color.material_teal_500) : widgetColor);
//
//        MDTintHelper.setTint(passwordInput,
//                widgetColor == 0 ? getResources().getColor(R.color.material_teal_500) : widgetColor);
//
//        dialog.show();
//        positiveAction.setEnabled(false); // disabled by default
//    }


}
