package org.buildmlearn.toolkit.templates;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.infotemplate.fragment.SplashFragment;
import org.buildmlearn.toolkit.model.Template;
import org.buildmlearn.toolkit.model.TemplateInterface;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;

/**
 * @brief Info template code implementing methods of TemplateInterface
 * <p/>
 * Created by abhishek on 16/06/15 at 9:59 PM.
 */
public class InfoTemplate implements TemplateInterface {

    transient private InfoAdapter adapter;
    private ArrayList<InfoModel> infoData;
    private int templateId;

    public InfoTemplate() {
        infoData = new ArrayList<>();
    }

    public static boolean validated(Context context, EditText word, EditText meaning) {
        if (word == null || meaning == null) {
            return false;
        }

        String wordText = word.getText().toString();
        String meaningText = meaning.getText().toString();

        if ("".equals(wordText.trim())) {
            Toast.makeText(context, "Enter a word.", Toast.LENGTH_SHORT).show();
            return false;
        } else if ("".equals(meaningText.trim())) {
            Toast.makeText(context, "Enter a description.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }

    @Override
    public BaseAdapter newTemplateEditorAdapter(Context context) {
        adapter = new InfoAdapter(context, infoData);
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
        infoData = new ArrayList<>();
        for (Element item : data) {
            String infoObject = item.getElementsByTagName("item_title").item(0).getTextContent();
            String infoDescription = item.getElementsByTagName("item_description").item(0).getTextContent();
            infoData.add(new InfoModel(infoObject, infoDescription));
        }
        adapter = new InfoAdapter(context, infoData);
        setEmptyView((Activity) context);
        return adapter;
    }

    @Override
    public void setTemplateId(int templateId) {
        this.templateId = templateId;
    }

    @Override
    public String getTitle() {
        return "Info Template";
    }

    @Override
    public void addItem(final Activity activity) {

        final MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .title(R.string.info_add_new_title)
                .customView(R.layout.info_dialog_add_edit_data, true)
                .positiveText(R.string.info_template_add)
                .negativeText(R.string.info_template_cancel)
                .build();

        final EditText word = (EditText) dialog.findViewById(R.id.info_word);
        final EditText meaning = (EditText) dialog.findViewById(R.id.info_meaning);

        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validated(activity, word, meaning)) {
                    String wordText = word.getText().toString().trim();
                    String meaningText = meaning.getText().toString().trim();

                    InfoModel temp = new InfoModel(wordText, meaningText);
                    infoData.add(temp);
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
        // This is intentionally empty
    }

    @Override
    public void editItem(final Activity activity, int position) {
        final MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .title(R.string.info_edit_title)
                .customView(R.layout.info_dialog_add_edit_data, true)
                .positiveText(R.string.info_template_ok)
                .negativeText(R.string.info_template_cancel)
                .build();

        final InfoModel data = infoData.get(position);

        final EditText word = (EditText) dialog.findViewById(R.id.info_word);
        final EditText meaning = (EditText) dialog.findViewById(R.id.info_meaning);
        word.setText(data.getInfoObject());
        meaning.setText(data.getInfoDescription());

        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validated(activity, word, meaning)) {
                    String wordText = word.getText().toString().trim();
                    String meaningText = meaning.getText().toString().trim();

                    data.setWord(wordText);
                    data.setInfoDescription(meaningText);

                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                }

            }
        });

        dialog.show();

    }

    @Override
    public void deleteItem(Activity activity, int position) {


        infoData.remove(position);
        setEmptyView(activity);
        adapter.notifyDataSetChanged();

    }

    @Override
    public ArrayList<Element> getItems(Document doc) {
        ArrayList<Element> itemElements = new ArrayList<>();


        for (InfoModel data : infoData) {

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
        return "BasicmLearningApp.apk";
    }

    @Override
    public void onActivityResult(Context context, int requestCode, int resultCode, Intent intent) {
        // This is intentionally empty
    }

    /**
     * @brief Toggles the visibility of empty text if Array has zero elements
     */
    private void setEmptyView(Activity activity) {
        if (infoData.size() < 1) {
            activity.findViewById(R.id.empty).setVisibility(View.VISIBLE);
        } else {
            activity.findViewById(R.id.empty).setVisibility(View.GONE);
        }
    }
}
