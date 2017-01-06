package org.buildmlearn.toolkit.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.model.SavedApi;
import org.buildmlearn.toolkit.views.TextViewPlus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @brief Adapter used for showing saved APIs in a list
 * <p/>
 * Created by opticod (Anupam Das) on 29/2/16.
 */

public class SavedApiAdapter extends BaseAdapter {

    private final Context mContext;
    private final ArrayList<SavedApi> data;
    private Map selectedPositions;

    public SavedApiAdapter(Context mContext, ArrayList<SavedApi> data) {
        this.mContext = mContext;
        this.data = data;
        selectedPositions = new HashMap();
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
    public SavedApi getItem(int i) {
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
        ApiHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_load_project, parent, false);
            holder = new ApiHolder();
            holder.apkName = (TextViewPlus) convertView.findViewById(R.id.title);
            holder.projectIcon = (TextViewPlus) convertView.findViewById(R.id.icon);
            holder.details = (TextViewPlus) convertView.findViewById(R.id.subtitle);
        } else {
            holder = (ApiHolder) convertView.getTag();
        }

        if (selectedPositions.containsKey(position)) {
            convertView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_divider));
        } else {
            convertView.setBackgroundColor(0);
        }

        SavedApi apiData = getItem(position);
        holder.details.setText(String.format(Locale.ENGLISH, "%s, %s", apiData.getAuthor(), apiData.getDate()));
        holder.apkName.setText(apiData.getName());
        holder.projectIcon.setText(apiData.getName().substring(0, 1).toUpperCase(Locale.US));
        convertView.setTag(holder);
        if (apiData.isSelected())
        {
            convertView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.color_divider));
        }
        return convertView;
    }

    public class ApiHolder {
        public TextViewPlus apkName;
        public TextViewPlus projectIcon;
        public TextViewPlus details;
    }
}
