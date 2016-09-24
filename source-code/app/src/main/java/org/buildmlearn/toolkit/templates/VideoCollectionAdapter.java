package org.buildmlearn.toolkit.templates;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.utilities.RoundedTransformation;
import org.buildmlearn.toolkit.views.TextViewPlus;

import java.util.ArrayList;

/**
 * @brief Adapter for displaying VideoCollection Template Editor data.
 * <p/>
 * Created by Anupam (opticod) on 4/5/16.
 */
class VideoCollectionAdapter extends BaseAdapter {

    private final Context mContext;
    private final ArrayList<VideoModel> data;

    public VideoCollectionAdapter(Context mContext, ArrayList<VideoModel> data) {
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public VideoModel getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        VideoTemplateHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.video_template_item, parent, false);
            holder = new VideoTemplateHolder();
        } else {
            holder = (VideoTemplateHolder) convertView.getTag();
        }

        holder.thumb = (ImageView) convertView.findViewById(R.id.thumb);
        holder.title = (TextViewPlus) convertView.findViewById(R.id.title);
        holder.description = (TextViewPlus) convertView.findViewById(R.id.description);

        VideoModel video = getItem(position);
        holder.description.setText(Html.fromHtml("<b>" + "Description :  " + "</b> " + video.getDescription()));
        holder.title.setText(Html.fromHtml("<b>" + "Title :  " + "</b> " + video.getTitle()));

        if (video.getThumbnailUrl().length() > 0) {
            Picasso
                    .with(mContext)
                    .load(video.getThumbnailUrl())
                    .transform(new RoundedTransformation(20, 20))
                    .fit()
                    .centerCrop()
                    .into(holder.thumb);
            holder.thumb.setAdjustViewBounds(true);
        }

        convertView.setTag(holder);
        return convertView;
    }

    public class VideoTemplateHolder {
        public ImageView thumb;
        public TextViewPlus title;
        public TextViewPlus description;
    }
}
