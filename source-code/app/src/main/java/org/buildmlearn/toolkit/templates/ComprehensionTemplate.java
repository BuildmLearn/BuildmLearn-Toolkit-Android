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
import org.buildmlearn.toolkit.model.TemplateInterface;
import org.buildmlearn.toolkit.videoCollectionTemplate.fragment.SplashFragment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * Created by Anupam (opticod) on 26/5/16.
 */
public class ComprehensionTemplate implements TemplateInterface {

    private final String TEMPLATE_NAME = "Comprehension Template";

    transient private ComprehensionAdapter adapter;
    transient private ComprehensionMetaAdapter metaAdapter;
    private ArrayList<ComprehensionModel> comprehensionData;
    private ArrayList<ComprehensionMetaModel> metaData;
    transient private Context mContext;

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

        if (titleText.equals("")) {
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
        mContext = context;
        adapter = new ComprehensionAdapter(context, comprehensionData);
        return adapter;
    }

    public BaseAdapter newMetaEditorAdapter(Context context) {
        mContext = context;
        metaAdapter = new ComprehensionMetaAdapter(context, metaData);
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
        long timer = Long.parseLong(doc.getElementsByTagName(ComprehensionMetaModel.TITLE_TAG).item(0).getTextContent());
        metaData.add(new ComprehensionMetaModel(title, passage, timer));
        metaAdapter = new ComprehensionMetaAdapter(context, metaData);

        return metaAdapter;

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
    }

    @Override
    public void addMetaData(final Activity activity) {
        final MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .title(R.string.comprehension_add_meta_title)
                .customView(R.layout.comprehension_meta_dialog_add_edit_data, true)
                .positiveText(R.string.info_template_add)
                .negativeText(R.string.info_template_cancel)
                .build();

        final EditText title = (EditText) dialog.findViewById(R.id.meta_title);
        final EditText passage = (EditText) dialog.findViewById(R.id.meta_passage);
        final EditText timer = (EditText) dialog.findViewById(R.id.meta_timer);

        dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validated(activity, title, passage, timer)) {

                    String titleText = title.getText().toString();
                    String passageText = passage.getText().toString();
                    long timerLong = Long.parseLong(timer.getText().toString());
                    ComprehensionMetaModel temp = new ComprehensionMetaModel(titleText, passageText, timerLong);
                    metaData.add(temp);
                    metaAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    @Override
    public void editItem(final Activity activity, final int position) {

    }

    @Override
    public void deleteItem(int position) {
        comprehensionData.remove(position);
        adapter.notifyDataSetChanged();
    }

    @Override
    public ArrayList<Element> getItems(Document doc) {
        ArrayList<Element> itemElements = new ArrayList<>();

        for (ComprehensionModel data : comprehensionData) {

            itemElements.add(data.getXml(doc));
        }

        return itemElements;
    }

    @Override
    public android.support.v4.app.Fragment getSimulatorFragment(String filePathWithName) {
        return SplashFragment.newInstance(filePathWithName);  //TODO:: Simulator
    }

    @Override
    public String getAssetsFileName() {
        return "comprehension_content.xml";
    }

    @Override
    public String getAssetsFilePath() {
        return "assets/";
    }

    @Override
    public String getApkFilePath() {
        return "VideoCollectionApp.apk";        //TODO:: ComprehensionApp.apk
    }

    @Override
    public void onActivityResult(Context context, int requestCode, int resultCode, Intent intent) {

    }
}
