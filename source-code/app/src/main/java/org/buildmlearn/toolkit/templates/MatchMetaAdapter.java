package org.buildmlearn.toolkit.templates;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.views.TextViewPlus;

import java.util.ArrayList;

/**
 * @brief Adapter for displaying Meta Details of Match The Following Template Editor data.
 * <p/>
 * Created by Anupam (opticod) on 16/7/16.
 */
class MatchMetaAdapter extends BaseAdapter {

    private final Context mContext;
    private final ArrayList<MatchMetaModel> data;

    public MatchMetaAdapter(Context mContext, ArrayList<MatchMetaModel> data) {
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public MatchMetaModel getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        MatchMetaHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.match_meta_item, parent, false);
            holder = new MatchMetaHolder();
            convertView.setTag(holder);
        } else {
            holder = (MatchMetaHolder) convertView.getTag();
        }

        holder.title = (TextViewPlus) convertView.findViewById(R.id.meta_title);
        holder.first_list_title = (TextViewPlus) convertView.findViewById(R.id.first_list_title);
        holder.second_list_title = (TextViewPlus) convertView.findViewById(R.id.second_list_title);

        MatchMetaModel meta = getItem(position);

        holder.title.setText(Html.fromHtml("<b>" + "Title :  " + "</b> " + meta.getTitle()));
        holder.first_list_title.setText(Html.fromHtml("<b>" + "First List Title :  " + "</b> " + meta.getFirstListTitle()));
        holder.second_list_title.setText(Html.fromHtml("<b>" + "Second List Title :  " + "</b> " + meta.getSecondListTitle()));

        return convertView;
    }

    public class MatchMetaHolder {
        public TextViewPlus title;
        public TextViewPlus first_list_title;
        public TextViewPlus second_list_title;
    }
}
