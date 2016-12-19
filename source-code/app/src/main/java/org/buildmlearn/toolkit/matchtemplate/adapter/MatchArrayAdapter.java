package org.buildmlearn.toolkit.matchtemplate.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.matchtemplate.Constants;

/**
 * Created by VOJJALA TEJA on 17-12-2016.
 */

public class MatchArrayAdapter extends CursorAdapter {

    public MatchArrayAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_dict, parent, false);
        org.buildmlearn.toolkit.matchtemplate.adapter.MatchArrayAdapter.ViewHolder viewHolder = new org.buildmlearn.toolkit.matchtemplate.adapter.MatchArrayAdapter.ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    /*
     *  This is where we fill-in the views with the contents of the cursor.
    */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final org.buildmlearn.toolkit.matchtemplate.adapter.MatchArrayAdapter.ViewHolder viewHolder = (org.buildmlearn.toolkit.matchtemplate.adapter.MatchArrayAdapter.ViewHolder) view.getTag();

        String title = cursor.getString(Constants.COL_TITLE_META);
        viewHolder.title.setText(title);

    }

    public static class ViewHolder {

        public final ImageView thumb;
        public final TextView title;

        public ViewHolder(View view) {
            thumb = (ImageView) view.findViewById(R.id.thumb);
            title = (TextView) view.findViewById(R.id.title);
        }
    }
}
