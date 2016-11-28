package org.buildmlearn.toolkit.templates;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.dictationtemplate.data.DictModel;
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
 * @brief Dictation template code implementing methods of TemplateInterface
 * <p/>
 * Created by Anupam (opticod) on 4/7/16.
 */
public class DictationTemplate implements TemplateInterface {

    transient private DictationAdapter adapter;
    private ArrayList<DictationModel> dictData;
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

        if ("".equals(titleText)) {
            Toast.makeText(context, R.string.dictation_template_title_hint, Toast.LENGTH_SHORT).show();
            return false;
        } else if ("".equals(passageText)) {
            Toast.makeText(context, R.string.dictation_template_passage_hint, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public BaseAdapter newTemplateEditorAdapter(Context context) {
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
    public String getTitle() {
        String TEMPLATE_NAME = "Dictation Template";
        return TEMPLATE_NAME;
    }

    @Override
    public void addItem(final Activity activity) {

        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dict_dialog_add_edit_data, null);
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.info_add_new_title)
                .setView(dialogView,
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_left),
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_top),
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_right),
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_bottom))
                .setPositiveButton(R.string.info_template_add, null)
                .setNegativeButton(R.string.info_template_cancel, null)
                .create();
        dialog.show();

        final EditText title = (EditText) dialogView.findViewById(R.id.dict_title);
        final EditText passage = (EditText) dialogView.findViewById(R.id.dict_passage);

        dialogView.findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileDialog fileDialog = new FileDialog(activity);
                fileDialog.setFileEndsWith();
                fileDialog.addFileListener(new FileDialog.FileSelectListener() {
                    public void fileSelected(File file) {
                        ((TextView) dialogView.findViewById(R.id.file_name)).setText(file.toString());
                        ((TextView) dialogView.findViewById(R.id.dict_passage)).setText(readFile(file));
                    }
                });
                fileDialog.showDialog();
            }
        });

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
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

    }

    @Override
    public void addMetaData(Activity activity) {

    }

    @Override
    public void editItem(final Activity activity, final int position) {

        LayoutInflater inflater = activity.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dict_dialog_add_edit_data, null);
        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.info_edit_title)
                .setView(dialogView,
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_left),
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_top),
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_right),
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_bottom))
                .setPositiveButton(R.string.info_template_ok, null)
                .setNegativeButton(R.string.info_template_cancel, null)
                .create();
        dialog.show();

        final DictationModel data = dictData.get(position);

        final EditText title = (EditText) dialogView.findViewById(R.id.dict_title);
        final EditText passage = (EditText) dialogView.findViewById(R.id.dict_passage);
        title.setText(data.getTitle());
        passage.setText(data.getPassage());

        dialogView.findViewById(R.id.upload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileDialog fileDialog = new FileDialog(activity);
                fileDialog.setFileEndsWith();
                fileDialog.addFileListener(new FileDialog.FileSelectListener() {
                    public void fileSelected(File file) {
                        ((TextView) dialogView.findViewById(R.id.file_name)).setText(file.toString());
                        ((TextView) dialogView.findViewById(R.id.dict_passage)).setText(readFile(file));
                    }
                });
                fileDialog.showDialog();
            }
        });

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
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
    }

    @Override
    public Object deleteItem(Activity activity, int position) {
        DictationModel dictationModel = dictData.get(position);
        dictData.remove(position);
        setEmptyView(activity);
        adapter.notifyDataSetChanged();
        return dictationModel;
    }

    @Override
    public void restoreItem(Activity activity, int position, Object object) {
        if (object instanceof DictationModel)
        {
            DictationModel dictationModel = (DictationModel)object;
            if (dictationModel!=null)
            {
                dictData.add(position,dictationModel);
                adapter.notifyDataSetChanged();
            }
        }
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
        // This is intentionally empty
    }

    /**
     * @brief Toggles the visibility of empty text if Array has zero elements
     */
    private void setEmptyView(Activity activity) {
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
