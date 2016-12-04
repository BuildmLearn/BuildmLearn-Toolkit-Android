package org.buildmlearn.toolkit.templates;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.comprehensiontemplate.fragment.SplashFragment;
import org.buildmlearn.toolkit.model.Template;
import org.buildmlearn.toolkit.model.TemplateInterface;
import org.buildmlearn.toolkit.utilities.FileDialog;
import org.buildmlearn.toolkit.views.TextViewPlus;
import android.support.v7.app.AlertDialog;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

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

        String titleText = title.getText().toString();
        String passageText = passage.getText().toString();
        String timerText = timer.getText().toString();

        if (timerText.length() > 9) {
            Toast.makeText(context, R.string.comprehension_template_timer_correct_hint, Toast.LENGTH_SHORT).show();
            return false;
        } else if ("".equals(titleText)) {
            Toast.makeText(context, R.string.comprehension_template_title_hint, Toast.LENGTH_SHORT).show();
            return false;
        } else if (passageText.equals("")) {
            Toast.makeText(context, R.string.comprehension_template_passage_hint, Toast.LENGTH_SHORT).show();
            return false;
        } else if (timerText.equals("")) {
            Toast.makeText(context, R.string.comprehension_template_timer_hint, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    @Override
    public BaseAdapter newTemplateEditorAdapter(Context context) {
        adapter = new ComprehensionAdapter(context, comprehensionData);
        return adapter;
    }

    public BaseAdapter newMetaEditorAdapter(Context context) {
        metaAdapter = new ComprehensionMetaAdapter(context, metaData);
        setEmptyView((Activity) context);
        return metaAdapter;
    }

    @Override
    public BaseAdapter currentTemplateEditorAdapter() {
        return adapter;
    }

    public BaseAdapter currentMetaEditorAdapter() {
        return metaAdapter;
    }

    @Override
    public BaseAdapter loadProjectTemplateEditor(Context context, ArrayList<Element> data) {
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
        adapter = new ComprehensionAdapter(context, comprehensionData);
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
        String TEMPLATE_NAME = "Comprehension Template";
        return TEMPLATE_NAME;
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

    @Override
    public void addItem(final Activity activity) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.quiz_dialog_add_question, null);
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
                    comprehensionData.add(new ComprehensionModel(questionText, answerOptions, correctAnswer));
                    setEmptyView(activity);
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void addMetaData(final Activity activity) {
        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.comprehension_meta_dialog_add_edit_data, null);
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

                    String titleText = title.getText().toString();
                    String passageText = passage.getText().toString();
                    long timerLong = Long.parseLong(timer.getText().toString());
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
            LayoutInflater inflater = activity.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.comprehension_meta_dialog_add_edit_data, null);
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
            passage.setText(data.getPassage());
            timer.setText(String.valueOf(data.getTime()));

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

                        String titleText = title.getText().toString();
                        String passageText = passage.getText().toString();
                        long timerLong = Long.parseLong(timer.getText().toString());

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

            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.quiz_dialog_add_question, null);
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
                        comprehensionData.set(position, new ComprehensionModel(questionText, answerOptions, correctAnswer));
                        adapter.notifyDataSetChanged();
                    }

                }
            });
        }
    }

    @Override
    public Object deleteItem(Activity activity, int position) {
        ComprehensionMetaModel comprehensionMetaModel =null;
        ComprehensionModel comprehensionModel=null;
        if (position == -2) {
            comprehensionMetaModel = metaData.get(0);
            metaData.remove(0);
            setEmptyView(activity);
            metaAdapter.notifyDataSetChanged();
        } else {
            comprehensionModel = comprehensionData.get(position);
            comprehensionData.remove(position);
            setEmptyView(activity);
            adapter.notifyDataSetChanged();
        }
        if (comprehensionMetaModel==null)
        {
            return comprehensionModel;
        }else
        {
            return comprehensionMetaModel;
        }
    }

    @Override
    public void restoreItem(Activity activity, int position, Object object) {
        if (position==-2)
        {
            if (object instanceof ComprehensionMetaModel)
            {
                ComprehensionMetaModel comprehensionMetaModel = (ComprehensionMetaModel)object;
                if (comprehensionMetaModel!=null)
                {
                    metaData.add(comprehensionMetaModel);
                    metaAdapter.notifyDataSetChanged();
                }
            }
        }else
        {
            if (object instanceof ComprehensionModel)
            {
                ComprehensionModel comprehensionModel = (ComprehensionModel)object;
                if (comprehensionModel!=null)
                {
                    comprehensionData.add(position,comprehensionModel);
                    adapter.notifyDataSetChanged();
                }
            }
        }

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

    /**
     * @brief Toggles the visibility of empty text if Array has zero elements
     */
    private void setEmptyView(Activity activity) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && metaData.size() > 0) {
            activity.findViewById(R.id.shadow_meta).setVisibility(View.VISIBLE);
        }
        if (comprehensionData.size() < 1 && metaData.size() < 1) {
            activity.findViewById(R.id.shadow_meta).setVisibility(View.GONE);
            ((TextViewPlus) activity.findViewById(R.id.empty_view_text)).setText(R.string.meta_add_help);
            activity.findViewById(R.id.empty).setVisibility(View.VISIBLE);
        } else if (comprehensionData.size() < 1) {
            ((TextViewPlus) activity.findViewById(R.id.empty_view_text)).setText(R.string.add_item_help);
            activity.findViewById(R.id.empty).setVisibility(View.VISIBLE);
        } else if (metaData.size() < 1) {
            activity.findViewById(R.id.shadow_meta).setVisibility(View.GONE);
            ((TextViewPlus) activity.findViewById(R.id.empty_view_text)).setText(R.string.meta_add_help);
            activity.findViewById(R.id.empty).setVisibility(View.VISIBLE);
        } else {
            activity.findViewById(R.id.empty).setVisibility(View.GONE);
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
