package org.buildmlearn.toolkit.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
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

/**
 * @brief Adapter used for showing Templates available in the toolkit
 * <p>
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
        }

        View view = inflater.inflate(layoutResource, viewGroup, false);
        RecyclerView.ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    public Template getItem(int i) {
        return templates[i];
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {

        ViewHolder vh = (ViewHolder) viewHolder;
        Template template = getItem(position);

        vh.getTitle().setText(template.getTitle());
        vh.getDescription().setText(template.getDescription());
        vh.getImage().setImageResource(template.getImage());

        int color = colors[position % colors.length].getColor();
        vh.getCardView().setCardBackgroundColor(color);
        vh.setItemClickListener(listener);

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
        void onItemClick(int position, View itemView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView description;
        public ImageView image;
        public CardView cardView;
        SetOnClickListener listener;

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
                        listener.onItemClick(getAdapterPosition(), v);
                    }
                }
            });
        }

        public ImageView getImage() {
            return image;
        }

        public void setImage(ImageView image) {
            this.image = image;
        }

        public CardView getCardView() {
            return cardView;
        }

        public void setCardView(CardView cardView) {
            this.cardView = cardView;
        }

        public TextView getDescription() {
            return description;
        }

        public void setDescription(TextView description) {
            this.description = description;
        }

        public TextView getTitle() {
            return title;
        }

        public void setTitle(TextView title) {
            this.title = title;
        }

        public void setItemClickListener(SetOnClickListener itemClickListener) {
            this.listener = itemClickListener;
        }

        public interface SetOnClickListener {
            void onItemClick(int position, View itemView);
        }
    }
}
