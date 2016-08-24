package org.buildmlearn.toolkit.templates;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.views.TextViewPlus;

import java.util.ArrayList;
import java.util.Locale;

/**
 * @brief Adapter for displaying Meta Details of Comprehension Template Editor data.
 * <p/>
 * Created by Anupam (opticod) on 26/5/16.
 */
class ComprehensionMetaAdapter extends BaseAdapter {

    private final Context mContext;
    private final ArrayList<ComprehensionMetaModel> data;

    public ComprehensionMetaAdapter(Context mContext, ArrayList<ComprehensionMetaModel> data) {
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public ComprehensionMetaModel getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ComprehensionMetaHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.comprehension_meta_item, parent, false);
            holder = new ComprehensionMetaHolder();
        } else {
            holder = (ComprehensionMetaHolder) convertView.getTag();
        }

        holder.title = (TextViewPlus) convertView.findViewById(R.id.title);
        holder.passage = (TextViewPlus) convertView.findViewById(R.id.passage);
        holder.timer = (TextViewPlus) convertView.findViewById(R.id.timer);

        ComprehensionMetaModel meta = getItem(position);

        holder.title.setText(meta.getTitle());
        holder.passage.setText(meta.getPassage());
        holder.timer.setText(String.format(Locale.ENGLISH, "%d sec", meta.getTime()));
        convertView.setTag(holder);

        return convertView;
    }

    public class ComprehensionMetaHolder {
        public TextViewPlus title;
        public TextViewPlus timer;
        public TextViewPlus passage;
    }
}
