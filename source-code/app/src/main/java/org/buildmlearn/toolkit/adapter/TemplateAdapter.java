package org.buildmlearn.toolkit.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.ColorRes;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.model.Template;

import java.lang.ref.WeakReference;

/**
 * @brief Adapter used for showing Templates available in the toolkit
 * <p/>
 * Created by Abhishek on 23-05-2015.
 */
public class TemplateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;

    private final Template[] templates = Template.values();
    private final ListColor[] colors = ListColor.values();
    private ViewHolder.SetOnClickListener listener;

    public TemplateAdapter(Context context) {
        this.context = context;
    }

    public static boolean cancelPotentialWork(int data, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final int bitmapData = bitmapWorkerTask.data;
            // If bitmapData is not yet set or it differs from the new data
            if (bitmapData == 0 || bitmapData != data) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    public void setOnClickListener(ViewHolder.SetOnClickListener clickListener) {
        this.listener = clickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        int layoutResource = 0;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        int viewType = getItemViewType(position);
        switch (viewType) {
            case 0:
                layoutResource = R.layout.item_template_right;
                break;
            case 1:
                layoutResource = R.layout.item_template_left;
                break;
            default: //do nothing
                break;
        }

        View view = inflater.inflate(layoutResource, viewGroup, false);

        return new ViewHolder(view);
    }

    private Template getItem(int i) {
        return templates[i];
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        ViewHolder vh = (ViewHolder) viewHolder;
        Template template = getItem(position);

        vh.getTitle().setText(template.getTitle());
        vh.getDescription().setText(template.getDescription());

        int color = colors[position % colors.length].getColor();
        vh.getCardView().setCardBackgroundColor(color);
        vh.setItemClickListener(listener);

        if (cancelPotentialWork(template.getImage(), vh.getImage())) {
            final BitmapWorkerTask task = new BitmapWorkerTask(context, vh.getImage());
            final AsyncDrawable asyncDrawable =
                    new AsyncDrawable(context.getResources(), null, task);
            vh.getImage().setImageDrawable(asyncDrawable);
            task.execute(template.getImage());
        }
    }

    @Override
    public int getItemCount() {
        return templates.length;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2;
    }

    enum ListColor {
        BLUE("#29A6D4"),
        GREEN("#1C7D6C"),
        ORANGE("#F77400"),
        RED("#F53B3C"),
        GRAYISH("#78909C"),
        PURPLE("#AB47BC"),
        YELLOW("#F9A01E"),
        YELLOW_GREEN("#9ACD32");

        private
        @ColorRes
        final
        int color;

        ListColor(String colorCode) {
            this.color = Color.parseColor(colorCode);
        }

        public int getColor() {
            return color;
        }
    }

    public interface SetOnClickListener extends ViewHolder.SetOnClickListener {
        void onItemClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView title;
        public final TextView description;
        public final ImageView image;
        public final CardView cardView;
        public SetOnClickListener listener;

        public ViewHolder(final View v) {
            super(v);
            title = ((TextView) v.findViewById(R.id.title));
            description = ((TextView) v.findViewById(R.id.description));
            image = ((ImageView) v.findViewById(R.id.image));
            cardView = ((CardView) v.findViewById(R.id.card_view));

            v.setClickable(true);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onItemClick(getAdapterPosition());
                    }
                }
            });
        }

        public ImageView getImage() {
            return image;
        }

        public CardView getCardView() {
            return cardView;
        }

        public TextView getDescription() {
            return description;
        }

        public TextView getTitle() {
            return title;
        }

        public void setItemClickListener(SetOnClickListener itemClickListener) {
            this.listener = itemClickListener;
        }

        public interface SetOnClickListener {
            void onItemClick(int position);
        }
    }

    static class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap,
                             BitmapWorkerTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapWorkerTaskReference =
                    new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return bitmapWorkerTaskReference.get();
        }
    }

    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {
        private final WeakReference<ImageView> imageViewReference;
        private int data = 0;
        private Context mContext;

        public BitmapWorkerTask(Context context, ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
            mContext = context;
            imageViewReference = new WeakReference<ImageView>(imageView);
        }

        // Decode image in background.
        @Override
        protected Bitmap doInBackground(Integer... params) {
            data = params[0];
            return decodeSampledBitmapFromResource(context.getResources(), data, 141, 180);
        }

        // Once complete, see if ImageView is still around and set bitmap.
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
            }

            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                final BitmapWorkerTask bitmapWorkerTask =
                        getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask && imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }

        public Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                      int reqWidth, int reqHeight) {

            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(res, resId, options);

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeResource(res, resId, options);
        }

        public int calculateInSampleSize(
                BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {

                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) >= reqHeight
                        && (halfWidth / inSampleSize) >= reqWidth) {
                    inSampleSize *= 2;
                }
            }

            return inSampleSize;
        }
    }
}
