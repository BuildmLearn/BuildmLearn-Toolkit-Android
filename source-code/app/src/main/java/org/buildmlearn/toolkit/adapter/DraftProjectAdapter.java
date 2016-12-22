package org.buildmlearn.toolkit.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.model.SavedProject;
import org.buildmlearn.toolkit.views.TextViewPlus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by scopeinfinity on 10/3/16.
 */

/**
 * @brief Adapter used for DraftsFragment to show the items
 */
public class DraftProjectAdapter extends BaseAdapter {

    private final Context mContext;
    private final ArrayList<SavedProject> data;
    private Map selectedPositions;

    public DraftProjectAdapter(Context mContext, ArrayList<SavedProject> data) {
        this.mContext = mContext;
        this.data = data;
        selectedPositions=new HashMap();
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

    public boolean isPositionSelected(int position)
    {
        return selectedPositions.containsKey(position);
    }

    public void putSelectedPosition(int position) {
        selectedPositions.put(position,true);
    }

    public ArrayList<Integer> getSelectedPositions()
    {
        ArrayList<Integer> positions = new ArrayList<>();
        for(Object key : selectedPositions.keySet())
        {
            positions.add((Integer)key);
        }
        Collections.sort(positions,Collections.reverseOrder());
        return positions;
    }

    public void removeSelectedPosition(int position)
    {
        selectedPositions.remove(position);
    }

    public int selectedPositionsSize()
    {
        return selectedPositions.size();
    }
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

        if (selectedPositions.containsKey(position)) {
            convertView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_divider));
        } else {
            convertView.setBackgroundColor(0);
        }

        SavedProject projectData = getItem(position);
        holder.draftSubtitle.setText(String.format(Locale.ENGLISH, "Last Modified: %s", projectData.getTime()));
        holder.draftTitle.setText(String.format(Locale.ENGLISH, "Drafted on %s", projectData.getDate()));
        holder.draftIcon.setText("D");
        convertView.setTag(holder);
        if (projectData.isSelected())
        {
            convertView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_divider));
        }
        return convertView;
    }

    public class DraftHolder {
        public TextViewPlus draftTitle;
        public TextViewPlus draftIcon;
        public TextViewPlus draftSubtitle;
    }
}
