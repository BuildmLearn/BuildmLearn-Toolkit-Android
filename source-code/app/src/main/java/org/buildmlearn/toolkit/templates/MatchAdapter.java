package org.buildmlearn.toolkit.templates;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.views.TextViewPlus;

import java.util.ArrayList;

/**
 * @brief Adapter for displaying Match Template Editor data.
 * <p/>
 * Created by Anupam (opticod) on 16/7/16.
 */
class MatchAdapter extends BaseAdapter {

    private final Context context;
    private final ArrayList<MatchModel> matchData;

    public MatchAdapter(Context context, ArrayList<MatchModel> matchData) {
        this.context = context;
        this.matchData = matchData;
    }

    @Override
    public int getCount() {
        return matchData.size();
    }

    @Override
    public MatchModel getItem(int position) {
        return matchData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater;
        mInflater = LayoutInflater.from(context);
        Holder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.match_item, parent, false);
            holder = new Holder();

            holder.matchA = (TextViewPlus) convertView.findViewById(R.id.matchA);
            holder.matchB = (TextViewPlus) convertView.findViewById(R.id.matchB);

        } else {
            holder = (Holder) convertView.getTag();
        }

        MatchModel data = getItem(position);
        holder.matchA.setText(data.getMatchA());
        holder.matchB.setText(data.getMatchB());

        convertView.setTag(holder);
        return convertView;
    }

    public class Holder {
        public TextViewPlus matchA;
        public TextViewPlus matchB;
    }
}
