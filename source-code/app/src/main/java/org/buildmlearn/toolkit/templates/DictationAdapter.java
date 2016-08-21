package org.buildmlearn.toolkit.templates;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.views.TextViewPlus;

import java.util.ArrayList;

/**
 * @brief Adapter for displaying Dictation Template Editor data.
 * <p/>
 * Created by Anupam (opticod) on 4/7/16.
 */
class DictationAdapter extends BaseAdapter {

    private final Context mContext;
    private final ArrayList<DictationModel> data;

    public DictationAdapter(Context mContext, ArrayList<DictationModel> data) {
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public DictationModel getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final DictationHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.dictation_template_item, parent, false);
            holder = new DictationHolder();
            convertView.setTag(holder);

        } else {
            holder = (DictationHolder) convertView.getTag();
        }

        holder.title = (TextViewPlus) convertView.findViewById(R.id.dict_title);
        holder.passage = (TextViewPlus) convertView.findViewById(R.id.dict_passage);
        holder.expandButton = (ImageButton) convertView.findViewById(R.id.toogle_expand);
        holder.collapseButton = (ImageButton) convertView.findViewById(R.id.toogle_collapse);

        final DictationModel dictation = getItem(position);
        holder.passage.setText(Html.fromHtml("<b>" + "Passage :  " + "</b> " + dictation.getPassage()));
        holder.title.setText(Html.fromHtml("<b>" + "Title :  " + "</b> " + dictation.getTitle()));

        if (dictation.isExpanded()) {
            holder.expandButton.setVisibility(View.INVISIBLE);
            holder.collapseButton.setVisibility(View.VISIBLE);

        } else {
            holder.expandButton.setVisibility(View.VISIBLE);
            holder.collapseButton.setVisibility(View.INVISIBLE);
        }
        holder.collapseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapseTextView(holder.passage);
                dictation.setExpanded(false);
                holder.expandButton.setVisibility(View.VISIBLE);
                holder.collapseButton.setVisibility(View.INVISIBLE);
            }
        });
        holder.expandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandTextView(holder.passage);
                dictation.setExpanded(true);
                holder.expandButton.setVisibility(View.INVISIBLE);
                holder.collapseButton.setVisibility(View.VISIBLE);
            }
        });

        return convertView;
    }

    private void expandTextView(TextView tv) {
        ObjectAnimator animation = ObjectAnimator.ofInt(tv, "maxLines", tv.getLineCount());
        animation.setDuration(tv.getLineCount() * 10).start();
    }

    private void collapseTextView(TextView tv) {
        ObjectAnimator animation = ObjectAnimator.ofInt(tv, "maxLines", 5);
        animation.setDuration(tv.getLineCount() * 10).start();
    }

    public class DictationHolder {
        public TextViewPlus title;
        public TextViewPlus passage;
        public ImageButton expandButton;
        public ImageButton collapseButton;
    }
}
