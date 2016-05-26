package org.buildmlearn.toolkit.templates;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.BaseAdapter;

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
    private ArrayList<ComprehensionModel> comprehensionData;
    transient private Context mContext;

    public ComprehensionTemplate() {
        comprehensionData = new ArrayList<>();
    }

    @Override
    public BaseAdapter newTemplateEditorAdapter(Context context) {
        mContext = context;
        adapter = new ComprehensionAdapter(context, comprehensionData);
        return adapter;
    }

    @Override
    public BaseAdapter currentTemplateEditorAdapter() {
        return adapter;
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
