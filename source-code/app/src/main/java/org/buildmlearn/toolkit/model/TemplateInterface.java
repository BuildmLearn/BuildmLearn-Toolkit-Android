package org.buildmlearn.toolkit.model;

import android.content.Context;
import android.widget.BaseAdapter;

import java.io.Serializable;

/**
 * Created by abhishek on 27/5/15.
 */
public interface TemplateInterface extends Serializable {

    public BaseAdapter newTemplateEditorAdapter(Context context);

    public BaseAdapter currentTemplateEditorAdapter();

    public String onAttach();

    public String getTitle();

    public void addItem(Context context);

    public void editItem(Context context, int position);

    public void deleteItem(int position);
    
}
