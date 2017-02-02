package org.buildmlearn.toolkit.templates;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.activity.TemplateEditorInterface;
import org.buildmlearn.toolkit.comprehensiontemplate.fragment.SplashFragment;
import org.buildmlearn.toolkit.model.Template;
import org.buildmlearn.toolkit.model.TemplateInterface;
import org.buildmlearn.toolkit.utilities.FileDialog;
import org.buildmlearn.toolkit.views.TextViewPlus;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

/**
 * @brief Comprehension template code implementing methods of TemplateInterface
 * <p/>
 * Created by Anupam (opticod) on 26/5/16.
 */
public class ComprehensionTemplate implements TemplateInterface {

    private final ArrayList<ComprehensionMetaModel> metaData;
    transient private ComprehensionAdapter adapter;
    transient private ComprehensionMetaAdapter metaAdapter;
    private ArrayList<ComprehensionModel> comprehensionData;
    private int templateId;

    public ComprehensionTemplate() {
        comprehensionData = new ArrayList<>();
        metaData = new ArrayList<>();
    }

    private static boolean validated(Context context, EditText title, EditText passage, EditText timer) {
        if (title == null || passage == null || timer == null) {
            return false;
        }

        String titleText = title.getText().toString().trim();
        String passageText = passage.getText().toString().trim();
        String timerText = timer.getText().toString().trim();

        if ("".equals(titleText)) {
            title.setError(context.getString(R.string.comprehension_template_title_hint));
            return false;
        } else if ("".equals(passageText)) {
            passage.setError(context.getString(R.string.comprehension_template_passage_hint));
            return false;
        } else if (timerText.length() > 9) {
            timer.setError(context.getString(R.string.comprehension_template_timer_correct_hint));
            return false;
        }else if (timerText.matches("[0]+")) {
            timer.setError((context.getString(R.string.time_zero_error)));
            return false;
        } else if ("".equals(timerText)) {
            timer.setError(context.getString(R.string.comprehension_template_timer_hint));
            return false;
        }

        return true;
    }

    @Override
    public Object newTemplateEditorAdapter(Context context, final TemplateEditorInterface templateEditorInterface) {
        adapter = new ComprehensionAdapter(context, comprehensionData) {
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
            protected void populateMetaList(ListView listView) {
                templateEditorInterface.populateMetaList(listView);
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
        return adapter;
    }

    public BaseAdapter newMetaEditorAdapter(Context context) {
        metaAdapter = new ComprehensionMetaAdapter(context, metaData);
        setEmptyView((Activity) context);
        return metaAdapter;
    }

    @Override
    public Object currentTemplateEditorAdapter() {
        return adapter;
    }

    public BaseAdapter currentMetaEditorAdapter() {
        return metaAdapter;
    }

    @Override
    public Object loadProjectTemplateEditor(Context context, ArrayList<Element> data, final TemplateEditorInterface templateEditorInterface) {
        comprehensionData = new ArrayList<>();
        for (Element item : data) {
            String question = item.getElementsByTagName("question").item(0).getTextContent();
            NodeList options = item.getElementsByTagName("option");
            ArrayList<String> answers = new ArrayList<>();
            for (int i = 0; i < options.getLength(); i++) {
                answers.add(options.item(i).getTextContent());
            }
            int answer = Integer.parseInt(item.getElementsByTagName("answer").item(0).getTextContent());
            comprehensionData.add(new ComprehensionModel(question, answers, answer));

        }
        adapter = new ComprehensionAdapter(context, comprehensionData) {
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
            protected void populateMetaList(ListView listView) {
                templateEditorInterface.populateMetaList(listView);
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
        return adapter;
    }

    public BaseAdapter loadProjectMetaEditor(Context context, Document doc) {

        String title = doc.getElementsByTagName(ComprehensionMetaModel.TITLE_TAG).item(0).getTextContent();
        String passage = doc.getElementsByTagName(ComprehensionMetaModel.PASSAGE_TAG).item(0).getTextContent();
        long timer = Long.parseLong(doc.getElementsByTagName(ComprehensionMetaModel.TIMER_TAG).item(0).getTextContent());
        metaData.add(new ComprehensionMetaModel(title, passage, timer));
        metaAdapter = new ComprehensionMetaAdapter(context, metaData);
        setEmptyView((Activity) context);

        return metaAdapter;

    }

    @Override
    public String getTitle() {

        return "Comprehension Template";
    }

    private void checkButton(ArrayList<RadioButton> buttons, ArrayList<EditText> options, int id, Context context) {
        for (RadioButton button : buttons) {
            if (button.getId() == id) {
                int index = buttons.indexOf(button);
                if ("".equals(options.get(index).getText().toString().trim())) {
                    options.get(index).setError(context.getString(R.string.valid_before_setting_answer));
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
    public void addItem(final Activity activity) {
        View dialogView = View.inflate(activity,R.layout.quiz_dialog_add_question,null);
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

                for (int i = 0; i < options.size(); i++) {
                    for (int j = 0; j < i; j++) {
                        if (!options.get(i).getText().toString().trim().isEmpty() && options.get(i).getText().toString().trim().equalsIgnoreCase(options.get(j).getText().toString().trim())) {
                            Toast.makeText(activity.getApplication(), activity.getString(R.string.same_options), Toast.LENGTH_SHORT).show();
                            isValidated=false;
                            return;
                        }
                    }
                }


                int correctAnswer = 0;
                int checkedAns = getCheckedAnswer(buttons);

                if (checkedAns < 0) {
                    Toast.makeText(activity, R.string.comprehension_template_choose_correct_option, Toast.LENGTH_SHORT).show();
                    isValidated = false;
                    return;
                }

                for (EditText option : options) {
                    if ("".equals(option.getText().toString().trim())) {
                        option.setText("");
                        continue;
                    }
                    if ("".equals(option.getText().toString().trim())) {
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
                    comprehensionData.add(new ComprehensionModel(questionText, answerOptions, correctAnswer));
                    setEmptyView(activity);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void addMetaData(final Activity activity) {
        final View dialogView = View.inflate(activity,R.layout.comprehension_meta_dialog_add_edit_data, null);
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.comprehension_add_meta_title)
                .setView(dialogView,
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_left),
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_top),
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_right),
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_bottom))
                .setPositiveButton(R.string.info_template_add, null)
                .setNegativeButton(R.string.info_template_cancel, null)
                .create();
        dialog.show();

        final EditText title = (EditText) dialogView.findViewById(R.id.meta_title);
        final EditText passage = (EditText) dialogView.findViewById(R.id.meta_passage);
        final EditText timer = (EditText) dialogView.findViewById(R.id.meta_timer);

        dialogView.findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileDialog fileDialog = new FileDialog(activity);
                fileDialog.setFileEndsWith();
                fileDialog.addFileListener(new FileDialog.FileSelectListener() {
                    public void fileSelected(File file) {
                        ((TextView) dialogView.findViewById(R.id.file_name)).setText(file.toString());
                        ((TextView) dialogView.findViewById(R.id.meta_passage)).setText(readFile(file));
                    }
                });
                fileDialog.showDialog();
            }
        });

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validated(activity, title, passage, timer)) {

                    String titleText = title.getText().toString().trim();
                    String passageText = passage.getText().toString().trim();
                    long timerLong = Long.parseLong(timer.getText().toString().trim());
                    ComprehensionMetaModel temp = new ComprehensionMetaModel(titleText, passageText, timerLong);
                    metaData.add(temp);
                    setEmptyView(activity);
                    metaAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }
        });
    }

    @Override
    public void editItem(final Activity activity, final int position) {
        if (position == -2) {

            final View dialogView = View.inflate(activity,R.layout.comprehension_meta_dialog_add_edit_data, null);
            final AlertDialog dialog = new AlertDialog.Builder(activity)
                    .setTitle(R.string.comprehension_edit_meta_title)
                    .setView(dialogView,
                            activity.getResources().getDimensionPixelSize(R.dimen.spacing_left),
                            activity.getResources().getDimensionPixelSize(R.dimen.spacing_top),
                            activity.getResources().getDimensionPixelSize(R.dimen.spacing_right),
                            activity.getResources().getDimensionPixelSize(R.dimen.spacing_bottom))
                    .setPositiveButton(R.string.info_template_ok, null)
                    .setNegativeButton(R.string.info_template_cancel, null)
                    .create();
            dialog.show();

            final ComprehensionMetaModel data = metaData.get(0);

            final EditText title = (EditText) dialogView.findViewById(R.id.meta_title);
            final EditText passage = (EditText) dialogView.findViewById(R.id.meta_passage);
            final EditText timer = (EditText) dialogView.findViewById(R.id.meta_timer);
            title.setText(data.getTitle());
            passage.setText(data.getPassage().trim());
            timer.setText(String.valueOf(data.getTime()).trim());

            dialogView.findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FileDialog fileDialog = new FileDialog(activity);
                    fileDialog.setFileEndsWith();
                    fileDialog.addFileListener(new FileDialog.FileSelectListener() {
                        public void fileSelected(File file) {
                            ((TextView) dialogView.findViewById(R.id.file_name)).setText(file.toString());
                            ((TextView) dialogView.findViewById(R.id.meta_passage)).setText(readFile(file));
                        }
                    });
                    fileDialog.showDialog();
                }
            });

            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (validated(activity, title, passage, timer)) {

                        String titleText = title.getText().toString().trim();
                        String passageText = passage.getText().toString().trim();
                        long timerLong = Long.parseLong(timer.getText().toString().trim());

                        data.setTitle(titleText);
                        data.setPassage(passageText);
                        data.setTime(timerLong);
                        metaAdapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                }
            });

        } else {
            ComprehensionModel data = comprehensionData.get(position);


            View dialogView = View.inflate(activity,R.layout.quiz_dialog_add_question,null);
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
                options.get(i).setText(data.getOptions().get(i));
            }

            question.setText(data.getQuestion().trim());
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
                        Toast.makeText(activity, R.string.comprehension_template_choose_correct_option, Toast.LENGTH_SHORT).show();
                        isValidated = false;
                        return;
                    }

                    for (EditText option : options) {
                        if ("".equals(option.getText().toString().trim())) {
                            option.setText("");
                            continue;
                        }
                        if ("".equals(option.getText().toString().trim())) {
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
                        comprehensionData.set(position, new ComprehensionModel(questionText, answerOptions, correctAnswer));
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    @Override
    public Object deleteItem(Activity activity, int position) {
        ComprehensionMetaModel comprehensionMetaModel = null;
        ComprehensionModel comprehensionModel = null;
        if (position == -2) {
            comprehensionMetaModel = metaData.get(0);
            metaData.remove(0);
            metaAdapter.notifyDataSetChanged();
        } else {
            comprehensionModel = comprehensionData.get(position);
            comprehensionData.remove(position);
        }
        adapter.notifyDataSetChanged();
        setEmptyView(activity);
        if (comprehensionMetaModel == null) {
            return comprehensionModel;
        } else {
            return comprehensionMetaModel;
        }
    }

    @Override
    public void restoreItem(Activity activity, int position, Object object) {
        if (position == -2) {
            if (object instanceof ComprehensionMetaModel) {
                ComprehensionMetaModel comprehensionMetaModel = (ComprehensionMetaModel) object;
                if (comprehensionMetaModel != null) {
                    metaData.add(comprehensionMetaModel);
                    metaAdapter.notifyDataSetChanged();
                }
            }
        } else {
            if (object instanceof ComprehensionModel) {
                ComprehensionModel comprehensionModel = (ComprehensionModel) object;
                if (comprehensionModel != null) {
                    comprehensionData.add(position, comprehensionModel);
                    adapter.notifyDataSetChanged();
                }
            }
        }
        setEmptyView(activity);
    }

    @Override
    public ArrayList<Element> getItems(Document doc) {
        ArrayList<Element> itemElements = new ArrayList<>();

        for (ComprehensionMetaModel data : metaData) {
            itemElements.add(data.getXml(doc));
        }

        for (ComprehensionModel data : comprehensionData) {

            itemElements.add(data.getXml(doc));
        }

        return itemElements;
    }

    @Override
    public android.support.v4.app.Fragment getSimulatorFragment(String filePathWithName) {
        return SplashFragment.newInstance(filePathWithName);
    }

    @Override
    public void setTemplateId(int templateId) {
        this.templateId = templateId;
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
        return "ComprehensionApp.apk";
    }

    @Override
    public void onActivityResult(Context context, int requestCode, int resultCode, Intent intent) {

    }

    @Override
    public boolean moveDown(Activity activity, int selectedPosition) {
        try {
            //Check already at last
            if (selectedPosition == comprehensionData.size() - 1)
                return false;
            Collections.swap(comprehensionData, selectedPosition, selectedPosition + 1);
            adapter.notifyDataSetChanged();
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
            Collections.swap(comprehensionData, selectedPosition, selectedPosition - 1);
            adapter.notifyDataSetChanged();
            return true;
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @brief Toggles the visibility of empty text if Array has zero elements
     */
    private void setEmptyView(Activity activity) {
        try {
            if (comprehensionData.size() < 1 && metaData.size() < 1) {
                ((TextViewPlus) activity.findViewById(R.id.empty_view_text)).setText(R.string.meta_add_help);
                activity.findViewById(R.id.empty).setVisibility(View.VISIBLE);
            } else if (comprehensionData.size() < 1) {
                ((TextViewPlus) activity.findViewById(R.id.empty_view_text)).setText(R.string.add_item_help);
                activity.findViewById(R.id.empty).setVisibility(View.VISIBLE);
            } else if (metaData.size() < 1) {
                ((TextViewPlus) activity.findViewById(R.id.empty_view_text)).setText(R.string.meta_add_help);
                activity.findViewById(R.id.empty).setVisibility(View.VISIBLE);
            } else {
                activity.findViewById(R.id.empty).setVisibility(View.GONE);
            }
        }catch (NullPointerException e)
        {
            e.printStackTrace();
        }
    }

    private String readFile(File file) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}