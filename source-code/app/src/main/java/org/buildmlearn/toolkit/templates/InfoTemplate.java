package org.buildmlearn.toolkit.templates;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.flashcardtemplate.StartFragment;
import org.buildmlearn.toolkit.model.TemplateInterface;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;

/**
 * Created by abhishek on 16/06/15 at 9:59 PM.
 */
public class InfoTemplate implements TemplateInterface {

    transient private InfoAdapter adapter;
    private ArrayList<InfoModel> infoData;

    public InfoTemplate() {
        infoData = new ArrayList<>();
    }

    @Override
    public BaseAdapter newTemplateEditorAdapter(Context context) {
        adapter = new InfoAdapter(context, infoData);
        return adapter;
    }

    @Override
    public BaseAdapter currentTemplateEditorAdapter() {
        return adapter;
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
        return adapter;
    }

    @Override
    public String onAttach() {
        return "Info Template";
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
                .negativeText(R.string.info_template_delete)
                .build();

        final EditText word = (EditText) dialog.findViewById(R.id.info_word);
        final EditText meaning = (EditText) dialog.findViewById(R.id.info_meaning);

        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validated(activity, word, meaning)) {
                    String wordText = word.getText().toString();
                    String meaningText = meaning.getText().toString();

                    InfoModel temp = new InfoModel(wordText, meaningText);
                    infoData.add(temp);
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                }

            }
        });

        dialog.show();

    }

    public static boolean validated(Context context, EditText word, EditText meaning) {
        if (word == null || meaning == null) {
            return false;
        }

        String wordText = word.getText().toString();
        String meaningText = meaning.getText().toString();

        if (wordText.equals("")) {
            Toast.makeText(context, "Enter word", Toast.LENGTH_SHORT).show();
            return false;
        } else if (meaningText.equals("")) {
            Toast.makeText(context, "Enter meaning", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }

    @Override
    public void editItem(final Context context, int position) {
        final MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title(R.string.info_add_new_title)
                .customView(R.layout.info_dialog_add_edit_data, true)
                .positiveText(R.string.info_template_add)
                .negativeText(R.string.info_template_delete)
                .build();

        final InfoModel data = infoData.get(position);

        final EditText word = (EditText) dialog.findViewById(R.id.info_word);
        final EditText meaning = (EditText) dialog.findViewById(R.id.info_meaning);
        word.setText(data.getInfoObject());
        meaning.setText(data.getInfoDescription());

        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validated(context, word, meaning)) {
                    String wordText = word.getText().toString();
                    String meaningText = meaning.getText().toString();

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
    public void deleteItem(int position) {


        infoData.remove(position);
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
    public Fragment getSimulatorFragment(String filePathWithName) {
        return StartFragment.newInstance(filePathWithName);
    }

    @Override
    public String getAssetsFileName() {
        return "info_content.xml";
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
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

    }
}
