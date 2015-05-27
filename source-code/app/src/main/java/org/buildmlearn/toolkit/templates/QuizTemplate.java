package org.buildmlearn.toolkit.templates;

import android.R;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import org.buildmlearn.toolkit.fragment.dummy.DummyContent;
import org.buildmlearn.toolkit.fragment.dummy.DummyContent.DummyItem;
import org.buildmlearn.toolkit.model.TemplateInterface;

/**
 * Created by abhishek on 27/5/15.
 */
public class QuizTemplate implements TemplateInterface {


    private ArrayAdapter<DummyItem> mAdapter;

    @Override
    public BaseAdapter newTemplateEditorAdapter(Context context) {
        mAdapter = new ArrayAdapter<>(context,
                android.R.layout.simple_list_item_1, R.id.text1, DummyContent.ITEMS);
        return mAdapter;
    }

    @Override
    public String onAttach() {
        return "This is Quiz Template";
    }

    @Override
    public String getTitle() {
        return "Quiz Template";
    }
}
