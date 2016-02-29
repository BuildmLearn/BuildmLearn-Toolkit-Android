package org.buildmlearn.toolkit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.model.SavedApi;
import org.buildmlearn.toolkit.views.TextViewPlus;

import java.util.ArrayList;
import java.util.Locale;

/**
 * @brief Adapter used for showing saved APIs in a list
 * <p/>
 * Created by opticod (Anupam Das) on 29/2/16.
 */

public class SavedApiAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<SavedApi> data;

    public SavedApiAdapter(Context mContext, ArrayList<SavedApi> data) {
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

        SavedApi apiData = getItem(position);
        holder.details.setText("Modified: " + apiData.getDate() + ", Author: " + apiData.getAuthor());
        holder.apkName.setText(apiData.getName());
        holder.projectIcon.setText(apiData.getName().substring(0, 1).toUpperCase(Locale.US));
        convertView.setTag(holder);
        return convertView;
    }

    public class ApiHolder {
        public TextViewPlus apkName;
        public TextViewPlus projectIcon;
        public TextViewPlus details;
    }
}
