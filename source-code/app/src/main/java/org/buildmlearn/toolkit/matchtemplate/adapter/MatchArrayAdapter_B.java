package org.buildmlearn.toolkit.matchtemplate.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.matchtemplate.data.MatchModel;

import java.util.ArrayList;

/**
 * Created by Anupam (opticod) on 24/7/16.
 */

/**
 * @brief Custom Adapter for match template's simulator.
 */
public class MatchArrayAdapter_B extends ArrayAdapter<MatchModel> {

    public MatchArrayAdapter_B(Context context, ArrayList<MatchModel> lists) {
        super(context, 0, lists);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MatchModel match = getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.match_template_list_item_info, parent, false);
            viewHolder.text = (TextView) convertView.findViewById(R.id.text);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.text.setText(match.getMatchB());
        if (match.getCorrect() == 2) {
            convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.item_correct_match));
        } else if (match.getCorrect() == 1) {
            convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.item_wrong_match));
        }

        return convertView;
    }

    public static class ViewHolder {

        public TextView text;
    }
}
