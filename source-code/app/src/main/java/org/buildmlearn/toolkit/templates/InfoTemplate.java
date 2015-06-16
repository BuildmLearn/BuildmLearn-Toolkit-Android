package org.buildmlearn.toolkit.templates;

import android.app.Fragment;
import android.content.Context;
import android.widget.BaseAdapter;

import org.buildmlearn.toolkit.model.TemplateInterface;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;

/**
 * Created by abhishek on 16/06/15 at 9:59 PM.
 */
public class InfoTemplate implements TemplateInterface {
    @Override
    public BaseAdapter newTemplateEditorAdapter(Context context) {
        return null;
    }

    @Override
    public BaseAdapter currentTemplateEditorAdapter() {
        return null;
    }

    @Override
    public BaseAdapter loadProjectTemplateEditor(Context context, ArrayList<Element> data) {
        return null;
    }

    @Override
    public String onAttach() {
        return null;
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public void addItem(Context context) {

    }

    @Override
    public void editItem(Context context, int position) {

    }

    @Override
    public void deleteItem(int position) {

    }

    @Override
    public ArrayList<Element> getItems(Document doc) {
        return null;
    }

    @Override
    public Fragment getSimulatorFragment(String filePathWithName) {
        return null;
    }

    @Override
    public String getAssetsFilePath() {
        return null;
    }

    @Override
    public String getApkFilePath() {
        return null;
    }
}
