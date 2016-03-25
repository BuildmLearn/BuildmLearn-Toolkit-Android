package org.buildmlearn.toolkit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.fragment.LoadProjectFragment;
import org.buildmlearn.toolkit.model.SavedProject;
import org.buildmlearn.toolkit.views.TextViewPlus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

/**
 * @brief Adapter used for showing saved projects in a list
 *
 * Created by Abhishek on 01-06-2015.
 */
public class SavedProjectAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<SavedProject> data;
    private int sortType;

    public SavedProjectAdapter(Context mContext, ArrayList<SavedProject> data) {
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

        SavedProject projectData = getItem(position);
        holder.details.setText("Modified: " + projectData.getDate() + ", Author: " + projectData.getAuthor());
        holder.projectName.setText(projectData.getName());
        holder.projectIcon.setText(projectData.getName().substring(0, 1).toUpperCase(Locale.US));
        convertView.setTag(holder);
        return convertView;
    }

    public void setSortType(int sortType) {
        this.sortType = sortType;
    }

    public int getSortType() {
        return sortType;
    }

    public void sort() {
        Comparator<SavedProject> comparator = null;
        switch (sortType) {
            case LoadProjectFragment.SORT_TYPE_MODIFIED :
                comparator = new Comparator<SavedProject>() {
                    public int compare(SavedProject f1, SavedProject f2) {
                        //f1 Modified later than f2
                        return Long.valueOf(f2.getFile().lastModified()).compareTo(f1.getFile().lastModified());
                    }
                };
                break;
            case LoadProjectFragment.SORT_TYPE_NAME :
                comparator = new Comparator<SavedProject>() {
                    public int compare(SavedProject f1, SavedProject f2) {
                        return f1.getName().compareToIgnoreCase(f2.getName());
                    }
                };
                break;
            case LoadProjectFragment.SORT_TYPE_AUTHOR :
                comparator = new Comparator<SavedProject>() {
                    public int compare(SavedProject f1, SavedProject f2) {
                        return f1.getAuthor().compareToIgnoreCase(f2.getAuthor());
                    }
                };
                break;

        }
        Collections.sort(data, comparator);
    }

    public class ProjectHolder {
        TextViewPlus projectName;
        TextViewPlus projectIcon;
        TextViewPlus details;
    }
}
