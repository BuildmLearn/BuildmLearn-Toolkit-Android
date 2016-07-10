package org.buildmlearn.toolkit.templates;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.dictationtemplate.fragment.SplashFragment;
import org.buildmlearn.toolkit.model.Template;
import org.buildmlearn.toolkit.model.TemplateInterface;
import org.buildmlearn.toolkit.utilities.FileDialog;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Anupam (opticod) on 4/7/16.
 */
public class DictationTemplate implements TemplateInterface {

    private final String TEMPLATE_NAME = "Dictation Template";
    transient private DictationAdapter adapter;
    private ArrayList<DictationModel> dictData;
    transient private Context mContext;
    private int templateId;

    public DictationTemplate() {
        dictData = new ArrayList<>();
    }

    private boolean validated(Context context, EditText title, EditText passage) {
        if (title == null || passage == null) {
            return false;
        }

        String titleText = title.getText().toString();
        String passageText = passage.getText().toString();

        if (titleText.equals("")) {
            Toast.makeText(context, R.string.dictation_template_title_hint, Toast.LENGTH_SHORT).show();
            return false;
        } else if (passageText.equals("")) {
            Toast.makeText(context, R.string.dictation_template_passage_hint, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public BaseAdapter newTemplateEditorAdapter(Context context) {
        mContext = context;
        adapter = new DictationAdapter(context, dictData);
        setEmptyView((Activity) context);
        return adapter;
    }

    @Override
    public BaseAdapter newMetaEditorAdapter(Context context) {
        return null;
    }

    @Override
    public BaseAdapter currentTemplateEditorAdapter() {
        return adapter;
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
    public BaseAdapter loadProjectTemplateEditor(Context context, ArrayList<Element> data) {
        mContext = context;
        dictData = new ArrayList<>();
        for (Element item : data) {
            String dictTitle = item.getElementsByTagName(DictationModel.TITLE_TAG).item(0).getTextContent();
            String dictPassage = item.getElementsByTagName(DictationModel.PASSAGE_TAG).item(0).getTextContent();
            dictData.add(new DictationModel(dictTitle, dictPassage));
        }
        adapter = new DictationAdapter(context, dictData);
        setEmptyView((Activity) context);
        return adapter;
    }

    @Override
    public String onAttach() {
        return TEMPLATE_NAME;
    }

    @Override
    public String getTitle() {
        return TEMPLATE_NAME;
    }

    @Override
    public void addItem(final Activity activity) {

        final MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .title(R.string.info_add_new_title)
                .customView(R.layout.dict_dialog_add_edit_data, true)
                .positiveText(R.string.info_template_add)
                .negativeText(R.string.info_template_cancel)
                .build();

        final EditText title = (EditText) dialog.findViewById(R.id.dict_title);
        final EditText passage = (EditText) dialog.findViewById(R.id.dict_passage);

        dialog.findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileDialog fileDialog = new FileDialog(activity);
                fileDialog.setFileEndsWith(".txt");
                fileDialog.addFileListener(new FileDialog.FileSelectListener() {
                    public void fileSelected(File file) {
                        ((TextView) dialog.findViewById(R.id.file_name)).setText(file.toString());
                        ((TextView) dialog.findViewById(R.id.dict_passage)).setText(readFile(file));
                    }
                });
                fileDialog.showDialog();
            }
        });

        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validated(activity, title, passage)) {
                    String titleText = title.getText().toString();
                    String passageText = passage.getText().toString();

                    DictationModel temp = new DictationModel(titleText, passageText);
                    dictData.add(temp);
                    adapter.notifyDataSetChanged();
                    setEmptyView(activity);

                    dialog.dismiss();
                }

            }
        });

        dialog.show();

    }

    @Override
    public void addMetaData(Activity activity) {

    }

    @Override
    public void editItem(final Activity activity, final int position) {

        final MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .title(R.string.info_edit_title)
                .customView(R.layout.dict_dialog_add_edit_data, true)
                .positiveText(R.string.info_template_ok)
                .negativeText(R.string.info_template_cancel)
                .build();

        final DictationModel data = dictData.get(position);

        final EditText title = (EditText) dialog.findViewById(R.id.dict_title);
        final EditText passage = (EditText) dialog.findViewById(R.id.dict_passage);
        title.setText(data.getTitle());
        passage.setText(data.getPassage());

        dialog.findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileDialog fileDialog = new FileDialog(activity);
                fileDialog.setFileEndsWith(".txt");
                fileDialog.addFileListener(new FileDialog.FileSelectListener() {
                    public void fileSelected(File file) {
                        ((TextView) dialog.findViewById(R.id.file_name)).setText(file.toString());
                        ((TextView) dialog.findViewById(R.id.dict_passage)).setText(readFile(file));
                    }
                });
                fileDialog.showDialog();
            }
        });

        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validated(activity, title, passage)) {

                    String titleText = title.getText().toString();
                    String passageText = passage.getText().toString();

                    data.setTitle(titleText);
                    data.setPassage(passageText);
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }
        });

        dialog.show();

    }

    @Override
    public void deleteItem(Activity activity, int position) {
        dictData.remove(position);
        setEmptyView(activity);
        adapter.notifyDataSetChanged();
    }

    @Override
    public ArrayList<Element> getItems(Document doc) {
        ArrayList<Element> itemElements = new ArrayList<>();

        for (DictationModel data : dictData) {

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
        return "DictationApp.apk";
    }

    @Override
    public void onActivityResult(Context context, int requestCode, int resultCode, Intent intent) {

    }

    /**
     * @brief Toggles the visibility of empty text if Array has zero elements
     */
    @Override
    public void setEmptyView(Activity activity) {
        if (dictData.size() < 1) {
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
