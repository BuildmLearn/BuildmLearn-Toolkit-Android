package org.buildmlearn.toolkit.videocollectiontemplate.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.utilities.RoundedTransformation;
import org.buildmlearn.toolkit.videocollectiontemplate.Constants;

/**
 * @brief Adapter for displaying VideoCollection Template Editor data in simulator.
 * <p/>
 * Created by Anupam (opticod) on 12/5/16.
 */
public class VideoArrayAdapter extends CursorAdapter {

    public VideoArrayAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_video, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    /*
     *  This is where we fill-in the views with the contents of the cursor.
    */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        final ViewHolder viewHolder = (ViewHolder) view.getTag();

        final String thumb_url = cursor.getString(Constants.COL_THUMBNAIL_URL);

        Picasso
                .with(context)
                .load(thumb_url)
                .transform(new RoundedTransformation(10, 10))
                .fit()
                .centerCrop()
                .into(viewHolder.thumb, new Callback() {
                    @Override
                    public void onSuccess() {
                        // This constructor is intentionally empty
                    }

                    @Override
                    public void onError() {
                        Picasso
                                .with(context)
                                .load(thumb_url)
                                .error(R.mipmap.ic_launcher)
                                .fit()
                                .centerCrop()
                                .into(viewHolder.thumb);
                    }
                });

        viewHolder.thumb.setAdjustViewBounds(true);

        String title = cursor.getString(Constants.COL_TITLE);
        viewHolder.title.setText(title);

        String description = cursor.getString(Constants.COL_DESCRIPTION);
        viewHolder.description.setText(Html.fromHtml(description));

    }

    public static class ViewHolder {

        public final ImageView thumb;
        public final TextView title;
        public final TextView description;

        public ViewHolder(View view) {
            thumb = (ImageView) view.findViewById(R.id.thumb);
            title = (TextView) view.findViewById(R.id.title);
            description = (TextView) view.findViewById(R.id.description);
        }
    }
}
