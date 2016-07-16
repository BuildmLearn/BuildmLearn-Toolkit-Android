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
import java.util.Locale;

/**
 * @brief Adapter used for showing saved projects in a list
 *
 * Created by Abhishek on 01-06-2015.
 */
public class SavedProjectAdapter extends BaseAdapter {

    private final Context mContext;
    private final ArrayList<SavedProject> data;
    private int selectedPosition;

    public SavedProjectAdapter(Context mContext, ArrayList<SavedProject> data) {
        this.mContext = mContext;
        this.data = data;
        selectedPosition = -1;
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

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater;
        mInflater = LayoutInflater.from(mContext);
        ProjectHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_load_project, parent, false);
            holder = new ProjectHolder();
            holder.projectName = (TextViewPlus) convertView.findViewById(R.id.title);
            holder.projectIcon = (TextViewPlus) convertView.findViewById(R.id.icon);
            holder.details = (TextViewPlus) convertView.findViewById(R.id.subtitle);
        } else {
            holder = (ProjectHolder) convertView.getTag();
        }

        if (selectedPosition == position) {
            convertView.setBackgroundColor(mContext.getResources().getColor(R.color.color_divider));
        } else {
            convertView.setBackgroundColor(0);
        }

        SavedProject projectData = getItem(position);
        holder.details.setText("Modified: " + projectData.getDate() + ", Author: " + projectData.getAuthor());
        holder.projectName.setText(projectData.getName());
        holder.projectIcon.setText(projectData.getName().substring(0, 1).toUpperCase(Locale.US));
        convertView.setTag(holder);
        return convertView;
    }

    public class ProjectHolder {
        public TextViewPlus projectName;
        public TextViewPlus projectIcon;
        public TextViewPlus details;
    }
}
