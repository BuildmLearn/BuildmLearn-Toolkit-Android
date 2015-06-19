package org.buildmlearn.toolkit.model;

import android.app.Fragment;
import android.content.Context;
import android.widget.BaseAdapter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by abhishek on 27/5/15.
 */
public interface TemplateInterface extends Serializable {

    BaseAdapter newTemplateEditorAdapter(Context context);

    BaseAdapter currentTemplateEditorAdapter();

    BaseAdapter loadProjectTemplateEditor(Context context, ArrayList<Element> data);

    String onAttach();

    String getTitle();

    void addItem(Context context);

    void editItem(Context context, int position);

    void deleteItem(int position);

    ArrayList<Element> getItems(Document doc);

    Fragment getSimulatorFragment(String filePathWithName);

    String getAssetsFileName();

    String getAssetsFilePath();

    String getApkFilePath();

}
