package org.buildmlearn.toolkit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.buildmlearn.toolkit.R;

/**
 * Created by Abhishek on 23-05-2015.
 */
public class TemplateAdapter extends BaseAdapter {

    private Context context;
    private int count;

    public TemplateAdapter(Context context, int count) {
        this.context = context;
        this.count = count;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if(true) {
            LayoutInflater mInflater;
            mInflater = LayoutInflater.from(context);

            if (i % 2 == 0) {
                view = mInflater.inflate(R.layout.item_template_right, viewGroup, false);
            } else {
                view = mInflater.inflate(R.layout.item_template_left, viewGroup, false);
            }
        }
        return view;
    }
}
