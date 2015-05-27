package org.buildmlearn.toolkit.model;

import android.content.Context;
import android.widget.BaseAdapter;

/**
 * Created by abhishek on 27/5/15.
 */
public interface TemplateInterface {

    public BaseAdapter newTemplateEditorAdapter(Context context);

    public String onAttach();

    public String getTitle();
    
}
