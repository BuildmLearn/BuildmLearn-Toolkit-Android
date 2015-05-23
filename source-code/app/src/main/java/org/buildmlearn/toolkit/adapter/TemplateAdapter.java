package org.buildmlearn.toolkit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.model.Template;

/**
 * Created by Abhishek on 23-05-2015.
 */
public class TemplateAdapter extends BaseAdapter {

    private Context context;
    private int count;

    private Template[] templates = Template.values();

    public TemplateAdapter(Context context, int count) {
        this.context = context;
        this.count = count;
    }

    @Override
    public int getCount() {
        return templates.length;
    }

    @Override
    public Template getItem(int i) {
        return templates[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater mInflater;
        mInflater = LayoutInflater.from(context);

        if (i % 2 == 0) {
            view = mInflater.inflate(R.layout.item_template_right, viewGroup, false);
        } else {
            view = mInflater.inflate(R.layout.item_template_left, viewGroup, false);
        }

        Template template = getItem(i);

        ((TextView) view.findViewById(R.id.title)).setText(template.getTitle());
        ((TextView) view.findViewById(R.id.description)).setText(template.getDescription());
        ((ImageView)view.findViewById(R.id.image)).setImageResource(template.getImage());

        return view;
    }
}
