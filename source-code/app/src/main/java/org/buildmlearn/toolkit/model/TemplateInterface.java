package org.buildmlearn.toolkit.model;

import android.content.Context;
import android.widget.BaseAdapter;

import java.io.Serializable;

/**
 * Created by abhishek on 27/5/15.
 */
public interface TemplateInterface extends Serializable {

    BaseAdapter newTemplateEditorAdapter(Context context);

    BaseAdapter currentTemplateEditorAdapter();

    String onAttach();

    String getTitle();

    void addItem(Context context);

    void editItem(Context context, int position);

    void deleteItem(int position);

    void saveProject(String name, String title);
    
}
