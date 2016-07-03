package org.buildmlearn.toolkit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.model.SavedProject;
import org.buildmlearn.toolkit.views.TextViewPlus;

import java.util.ArrayList;

/**
 * Created by scopeinfinity on 10/3/16.
 */
public class DraftProjectAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<SavedProject> data;

    public DraftProjectAdapter(Context mContext, ArrayList<SavedProject> data) {
        this.mContext = mContext;
        this.data = data;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCount() {
        return data.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SavedProject getItem(int i) {
        return data.get(i);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getItemId(int i) {
        return i;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater;
        mInflater = LayoutInflater.from(mContext);
        DraftHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_load_project, parent, false);
            holder = new DraftHolder();
            holder.draftTitle = (TextViewPlus) convertView.findViewById(R.id.title);
            holder.draftIcon = (TextViewPlus) convertView.findViewById(R.id.icon);
            holder.draftSubtitle = (TextViewPlus) convertView.findViewById(R.id.subtitle);
        } else {
            holder = (DraftHolder) convertView.getTag();
        }

        SavedProject projectData = getItem(position);
        holder.draftSubtitle.setText("Last Modified: " + projectData.getTime() );
        holder.draftTitle.setText("Drafted on "+projectData.getDate());
        holder.draftIcon.setText("D");
        convertView.setTag(holder);
        return convertView;
    }

    public class DraftHolder {
        public TextViewPlus draftTitle;
        public TextViewPlus draftIcon;
        public TextViewPlus draftSubtitle;
    }
}
